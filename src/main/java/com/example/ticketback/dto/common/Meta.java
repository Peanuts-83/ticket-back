package com.example.ticketback.dto.common;

import com.example.ticketback.domain.enums.MetaType;

import java.util.List;

/**
 * Métadonnées génériques d'un champ
 * @param type type technique du champ
 * @param values pour les types
 * @param defaultValue pour les types
 * @param libelle nom du champ à afficher
 */
public record Meta (
        MetaType type,
        List<String> values,
        Object defaultValue,
        String libelle
) {
}
