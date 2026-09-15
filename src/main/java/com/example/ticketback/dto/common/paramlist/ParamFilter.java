package com.example.ticketback.dto.common.paramlist;

import com.example.ticketback.dto.common.paramlist.constants.Combinator;
import com.example.ticketback.dto.common.paramlist.constants.Operator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Filtres de liste.
 * Un filtre est soit une feuille, soit un groupe, on ne combine pas les 2.
 * */
@Schema(description = "Filtres d'une requête de liste")
public record ParamFilter(
        @Schema(description = "Champ de la table")
        String fieldName,
        @Schema(description = "Opérateur SQL")
        Operator fieldOperator,
        @Schema(description = "Valeur du champ")
        Object value,
        @Schema(description = "Liste de filtres")
        List<ParamFilter> filterList,
        @Schema(description = "Opérateur de combinaison de liste de filtres")
        Combinator listCombinator
) {
        public boolean isGroupe() {
                return filterList != null && !filterList.isEmpty();
        }

        public ParamFilter {
                if (filterList != null && !filterList.isEmpty() && fieldName != null) {
                        throw new IllegalArgumentException("Un filtre est soit une feuille soit un groupe.");
                }
        }
}
