package com.example.ticketback.dto.common.paramlist.constants;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Articulation d'une liste de filtres")
public enum Combinator {
    AND,
    OR
}
