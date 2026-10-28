package com.example.ticketback.dto.common;

import com.example.ticketback.dto.common.paramlist.ParamFilterTranslator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

/**
 * Socle générique des services de liste.
 * <p>
 * Mutualise, pour une entité {@code T} et son DTO {@code U}, les responsabilités communes à
 * tout écran liste : pagination, filtres dynamiques et projection en DTO. Un service concret
 * hérite de cette classe, fournit son repository et sa liste blanche de champs, puis expose
 * {@link #getListWithFilters(BaseHttpParams)} depuis sa méthode {@code getList}.
 *
 * @param <T> type de l'entité JPA
 * @param <U> type du DTO exposé au front
 */
@RequiredArgsConstructor
public abstract class BaseListService<T,U> {
    protected final ParamFilterTranslator translator;

    /** Repository de l'entité ; doit supporter les {@link Specification} JPA. */
    protected abstract JpaSpecificationExecutor<T> executor();

    /**
     * Liste blanche des champs filtrables et triables. Le nom de champ vient du client :
     * sans cette garde, il pourrait filtrer ou trier sur n'importe quel attribut de l'entité.
     */
    protected abstract Set<String> authorizedFields();

    /**
     * Tri appliqué quand le client n'en fournit aucun. <b>Obligatoire</b> : sans {@code ORDER BY},
     * la pagination n'est pas déterministe (une même ligne peut réapparaître d'une page à l'autre).
     */
    protected abstract Sort defaultSort();

    /** Projection entité → DTO (on n'expose jamais l'entité JPA directement). */
    protected abstract U toDto(T entity);

    /**
     * Liste paginée, filtrée et triée.
     * <p>
     * Résout les paramètres (valeurs par défaut si {@code a_params} est nul), valide les champs de
     * tri contre {@link #authorizedFields()}, construit le {@link Pageable} (page 0-based, tri du
     * client ou {@link #defaultSort()}), traduit les filtres en {@link Specification} bornée par la
     * même liste blanche, puis projette chaque entité via {@link #toDto(Object)}.
     *
     * @param a_params paramètres de requête (peut être nul)
     * @return la page de DTO demandée
     * @throws org.springframework.web.server.ResponseStatusException 400 si un champ de tri n'est pas autorisé
     */
    protected List<U> getListWithFilters(@Nullable BaseHttpParams a_params) {
        BaseHttpParamList l_paramList = a_params != null ? a_params.resolvedparamList() : BaseHttpParamList.defaultValue();
        validateSortFields(l_paramList);
        Pageable l_pageable = PageRequest.of(l_paramList.resolvedPageNum(), l_paramList.resolvedNb(), l_paramList.resolvedSort(defaultSort()));
        Specification<T> l_spec = translator.toSpecification(l_paramList.filters(), authorizedFields());
        return executor().findAll(l_spec, l_pageable).stream().map(this::toDto).toList();
    }

    /** Rejette en 400 tout champ de tri hors de la liste blanche. */
    private void validateSortFields(BaseHttpParamList a_paramList) {
        if (a_paramList.sorts() == null) return;
        a_paramList.sorts().forEach(b_sort -> {
            if (!authorizedFields().contains(b_sort.fieldName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champ de tri invalide: " + b_sort.fieldName());
            }
        });
    }
}
