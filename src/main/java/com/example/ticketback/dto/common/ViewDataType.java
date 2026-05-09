package com.example.ticketback.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Contexte de vue demandée par le front.
 * ex:
 * {
 *     "dataType": "liste"
 * }
 */
public enum ViewDataType {
    LISTE("liste"),
    UPDATE("update"),
    CREATE("CREATE"),
    DELETE("delete");

    private final String value;

    ViewDataType(String value) {
        this.value = value;
    }

    // Sérialisation Jackson JSON Java -> JSON
    @JsonValue
    public String getValue() {
        return value;
    }

    // Désérialisation Jackson JSON -> Java
    @JsonCreator
    public static ViewDataType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ViewDataType item : ViewDataType.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }
}
