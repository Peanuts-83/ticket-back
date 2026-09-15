package com.example.ticketback.dto.common.paramlist;

import com.example.ticketback.dto.common.paramlist.constants.Combinator;
import com.example.ticketback.dto.common.paramlist.constants.Operator;
import jakarta.persistence.criteria.*;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interpréteur de filtres (ParamFilter)
 *
 * @notion authorizedFields - liste des champs filtrables issue du service
 * @notion CriteriaQuery - requête en cours de construction
 * @notion CriteriaBuilder - factory qui fournit Root/Path/Expression(Calcul sur champ)/Predicate(condition bool)
 * @notion Root - Table SQL
 * @notion Path - champ/colonne de la table
 *
 * @example Filtrer sur le statut
 * { "fieldName": "status", "fieldOperator": "EQUAL", "value": "DONE" }
 * Root = Ticket - Path = status - CriteriaeQuery = where t1_0.status = 'DONE'
 *
 * @exammple Recherche textuelle sur le titre
 * { "fieldName": "title", "fieldOperator": "LIKE", "value": "connexion" }
 * Root = Ticket - Path = title - CriteriaeQuery = where lower(t1_0.title) like '%connexion%' escape '!'
 */
@Component
public class ParamFilterTranslator {
    // Service de conversion Spring String vers enum, LocalDateTime etc...
    private final ConversionService conversionService =  new DefaultFormattingConversionService();

    private static final char ESCAPE_LIKE = '!';

    public <T> Specification<T> toSpecification(ParamFilter a_filter, Set<String> a_authorizedFields) {
        if (a_filter == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> toPredicate(a_filter, root, cb, a_authorizedFields);
    }

    private <T> Predicate toPredicate(ParamFilter a_filter, Root<T> root, CriteriaBuilder cb, Set<String> a_authorizedFields) {
        if (a_filter.isGroupe()) {
            Predicate[] l_children = a_filter.filterList().stream()
                    .map(child -> toPredicate(child, root, cb, a_authorizedFields))
                    .toArray(Predicate[]::new);
            return a_filter.listCombinator() == Combinator.OR ? cb.or(l_children) : cb.and(l_children);
        }
        return filtreSimple(a_filter, root, cb, a_authorizedFields);
    }

    private <T> Predicate filtreSimple(ParamFilter a_filter, Root<T> root, CriteriaBuilder cb,  Set<String> a_authorizedFields) {
        if (a_filter.fieldName() == null || !a_authorizedFields.contains(a_filter.fieldName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champ non filtrable: " + a_filter.fieldName());
        }
        if (a_filter.fieldOperator() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opérateur de filtre absent sur le champ: " + a_filter.fieldName());
        }
        Path<Object> l_col = root.get(a_filter.fieldName());
        Class<?> l_type = ClassUtils.resolvePrimitiveIfNecessary(l_col.getJavaType());

        return switch (a_filter.fieldOperator()) {
            case EQUAL -> cb.equal(l_col, valeur(a_filter.value(), a_filter, l_type));
            case NOT_EQUAL -> cb.notEqual(l_col, valeur(a_filter.value(), a_filter, l_type));
            case LESS_THAN -> compare(cb, l_col, valeur(a_filter.value(), a_filter, l_type), Operator.LESS_THAN);
            case LESS_OR_EQUAL -> compare(cb, l_col, valeur(a_filter.value(), a_filter, l_type), Operator.LESS_OR_EQUAL);
            case GREATER_THAN -> compare(cb, l_col, valeur(a_filter.value(), a_filter, l_type), Operator.GREATER_THAN);
            case GREATER_OR_EQUAL -> compare(cb, l_col, valeur(a_filter.value(), a_filter, l_type), Operator.GREATER_OR_EQUAL);
            case IN -> l_col.in(valeurList(a_filter, l_type));
            case NOT_IN -> cb.not(l_col.in(valeurList(a_filter, l_type)));
            case BETWEEN -> between(cb, l_col, a_filter,l_type, true);
            case NOT_BETWEEN -> between(cb, l_col, a_filter,l_type, false);
            case LIKE -> like(cb, l_col, "%"+ textLike(a_filter)+"%");
            case NOT_LIKE -> notLike(cb, l_col, "%"+ textLike(a_filter)+"%");
            case STARTS_WITH -> like(cb, l_col, textLike(a_filter)+"%");
            case ENDS_WITH -> like(cb, l_col, "%"+ textLike(a_filter));
            case NOT_STARTS_WITH -> notLike(cb, l_col, textLike(a_filter)+"%");
            case NOT_ENDS_WITH -> notLike(cb, l_col, "%"+ textLike(a_filter));
            case IS_NULL -> cb.isNull(l_col);
            case IS_NOT_NULL -> cb.isNotNull(l_col);
            case IS_EMPTY -> cb.length(l_col.as(String.class)).equalTo(0);
            case IS_NOT_EMPTY -> cb.length(l_col.as(String.class)).notEqualTo(0);
        };
    }

    /** HELPERS */
    private String escapeLike(String a_value) {
        String esc = String.valueOf(ESCAPE_LIKE);
        return a_value
                .replace(esc, esc+esc)
                .replace("_", esc+"_")
                .replace("%", esc+"%");
    }

    private String textLike(ParamFilter a_filter) {
        if (!(a_filter.value() instanceof String a_value) || a_value.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'opérateur " + a_filter.fieldOperator() + " attend une valeur texte non vide pour le champ " + a_filter.fieldName());
        }
        return Arrays.stream(a_value.split(""))
                .map(this::escapeLike)
                .collect(Collectors.joining());
    }

    private <Y extends Comparable<? super Y>> Predicate between(CriteriaBuilder cb, Path<Object> a_col, ParamFilter a_filter, Class<?> a_type, boolean a_between) {
        List<Object> l_bornes = valeurList(a_filter, a_type);
        if (l_bornes.size() != 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deux bornes sont requises pour le champ " + a_filter.fieldName());
        }
        Predicate l_between = safeBetween(cb, a_col, l_bornes.get(0), l_bornes.get(1));
        return a_between ?
                 l_between : cb.not(l_between);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate compare(CriteriaBuilder cb, Path<Object> a_col, Object a_v, Operator a_operator) {
        return switch (a_operator) {
            case LESS_THAN -> cb.lessThan((Expression<Y>) (Expression<?>)a_col, (Y)a_v);
            case LESS_OR_EQUAL -> cb.lessThanOrEqualTo((Expression<Y>) (Expression<?>)a_col, (Y)a_v);
            case GREATER_THAN -> cb.greaterThan((Expression<Y>) (Expression<?>)a_col, (Y)a_v);
            case GREATER_OR_EQUAL -> cb.greaterThanOrEqualTo((Expression<Y>) (Expression<?>)a_col, (Y)a_v);
            default -> throw new IllegalArgumentException("Opérateur non autorisé: " + a_operator);
        };
    }

    /**
     * Convertit une liste de valeurs
     * @param a_filter
     * @param a_type
     * @return
     */
    private List<Object> valeurList(ParamFilter a_filter, Class<?> a_type) {
        if (!(a_filter.value() instanceof Collection<?> l_collection) || l_collection.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, a_filter.fieldOperator() + "attend une liste de valeurs pour le champ " + a_filter.fieldName());
        }
        return l_collection.stream()
                .map(v -> valeur(v, a_filter, a_type))
                .toList();
    }

    /**
     * Convertit la valeur scalaire d'un champ,
     * avec garde de nullité
     * et capture de conversionException
      */
    private Object valeur(Object a_obj, ParamFilter a_filter, Class<?> a_type) {
        if (a_obj == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valeur nulle interdite sur le champ" + a_filter.fieldName() + ", utiliser IS_NULL");
        }
        if (a_type.isInstance(a_obj)) {
            return a_obj;
        }
        Object l_convert;
        try {
            l_convert = conversionService.canConvert(a_obj.getClass(), a_type) ? conversionService.convert(a_obj, a_type) : null;
        } catch(ConversionException e) {
            l_convert = null;
        }
        if (l_convert == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valeur " + a_obj + " incompatible avec le champ " + a_filter.fieldName() + " (Type " + a_type.getSimpleName() + ")");
        }
        return l_convert;
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate safeBetween(CriteriaBuilder cb, Path<Object> a_col,
                                                                Object a_min, Object a_max) {
        return cb.between((Expression<Y>) (Expression<?>) a_col, (Y) a_min, (Y) a_max);
    }

    private <T> Predicate like(CriteriaBuilder cb, Path<Object> a_col, String a_motif) {
        return cb.like(cb.lower(a_col.as(String.class)), a_motif.toLowerCase(), ESCAPE_LIKE);
    }

    private <T> Predicate notLike(CriteriaBuilder cb, Path<Object> a_col, String a_motif) {
        return cb.notLike(cb.lower(a_col.as(String.class)), a_motif.toLowerCase(), ESCAPE_LIKE);
    }

}

/**
 * filtreSimple - Explications
 *
 * Path<Object> l_col = root.get(a_filter.fieldName());
 * Class<?>     l_type = l_col.getJavaType();
 *
 * - root est le Root<Ticket> : la table tickets dans le FROM de la requête en construction.
 * - l_col n'est pas une valeur, c'est une référence de colonne — un nœud dans l'arbre Criteria. Il ne contient rien, il désigne. À la génération du SQL, il devient t1_0.status. C'est ce que tu passes en premier argument de cb.equal(...), cb.like(
 * ...)
 * , etc. : le côté gauche de la comparaison.
 * - l_type est le type Java de l'attribut, lu dans le métamodèle JPA — c'est-à-dire la déclaration du champ dans Ticket, pas le type de ce que le client a envoyé. C'est la cible de conversion : le client envoie toujours du JSON brut (String, Integer, ArrayList), il faut le transformer en ce que la colonne attend.
 *
 * Deux exemples tracés sur Ticket
 *
 * 1. Filtrer sur le statut
 *
 * { "fieldName": "status", "fieldOperator": "EQUAL", "value": "DONE" }
 *
 * ┌───────────────────┬─────────────────────────────────────────────────────────────────┐
 * │                   │                                                                 │
 * ├───────────────────┼─────────────────────────────────────────────────────────────────┤
 * │ l_col             │ référence vers Ticket.status → SQL t1_0.status                  │
 * ├───────────────────┼─────────────────────────────────────────────────────────────────┤
 * │ l_type            │ TicketStatus.class (le champ est private TicketStatus status)   │
 * ├───────────────────┼─────────────────────────────────────────────────────────────────┤
 * │ a_filter.value()  │ "DONE" — une String, Jackson ne sait pas mieux depuis un Object │
 * ├───────────────────┼─────────────────────────────────────────────────────────────────┤
 * │ après valeur(...) │ TicketStatus.DONE — le ConversionService fait String → enum     │
 * ├───────────────────┼─────────────────────────────────────────────────────────────────┤
 * │ SQL               │ where t1_0.status = 'DONE'                                      │
 * └───────────────────┴─────────────────────────────────────────────────────────────────┘
 *
 * C'est le cas qui justifie tout le mécanisme : sans la conversion, cb.equal(path, "DONE") lève Parameter value [DONE] did not match expected type [TicketStatus].
 *
 * 2. Recherche textuelle sur le titre
 *
 * { "fieldName": "title", "fieldOperator": "LIKE", "value": "connexion" }
 *
 * ┌────────────┬────────────────────────────────────────────────────────────────────────────────┐
 * │            │                                                                                │
 * ├────────────┼────────────────────────────────────────────────────────────────────────────────┤
 * │ l_col      │ Ticket.title → t1_0.title                                                      │
 * ├────────────┼────────────────────────────────────────────────────────────────────────────────┤
 * │ l_type     │ String.class                                                                   │
 * ├────────────┼────────────────────────────────────────────────────────────────────────────────┤
 * │ conversion │ aucune — a_type.isInstance(l_brut) est vrai, on renvoie la valeur telle quelle │
 * ├────────────┼────────────────────────────────────────────────────────────────────────────────┤
 * │ SQL        │ where lower(t1_0.title) like '%connexion%' escape '!'                          │
 * └────────────┴────────────────────────────────────────────────────────────────────────────────┘
 *
 * Ici l_type sert surtout au garde-fou : verifierTexte(l_type) refuse un LIKE sur un champ non textuel, ce qui évite un cast implicite en base sur un enum ou une date.
 *
 */