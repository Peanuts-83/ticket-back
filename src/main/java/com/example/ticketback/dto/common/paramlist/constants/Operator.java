package com.example.ticketback.dto.common.paramlist.constants;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Opérateurs SQL dédiés aux filtres de liste")
public enum Operator {
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_OR_EQUAL,
    GREATER_THAN,
    GREATER_OR_EQUAL,
    IN,
    NOT_IN,
    BETWEEN,
    NOT_BETWEEN,
    LIKE,
    NOT_LIKE,
    STARTS_WITH,
    ENDS_WITH,
    NOT_STARTS_WITH,
    NOT_ENDS_WITH,
    IS_NULL,
    IS_NOT_NULL,
    IS_EMPTY,
    IS_NOT_EMPTY
}
