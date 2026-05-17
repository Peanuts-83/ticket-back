package com.example.ticketback.dto.common;

import com.example.ticketback.domain.enums.MetaType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Métadonnées génériques d'un champ
 * @param type type technique du champ
 * @param values pour les types
 * @param defaultValue pour les types
 * @param libelle nom du champ à afficher
 */
@Schema(description = "Métadonnées spécifiques à chaque champ")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Meta (
        @Schema(description = "Type du champ")
        MetaType type,
        @Schema(description = "Valeur possibles pour les enums")
        List<String> values,
        @Schema(description = "Valeur par défaut")
        Object defaultValue,
        @Schema(description = "Libellé du champ")
        String libelle
) {
}
