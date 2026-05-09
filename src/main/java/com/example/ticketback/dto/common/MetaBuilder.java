package com.example.ticketback.dto.common;

import com.example.ticketback.domain.enums.MetaType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Générateur automatique de métadonnées des champs.
 *
 * Objectif:
 * - déduire les metas à partir d'un DTO ou d'une classe.
 * - possibilité d'enrichir certains champs avec @MetaField.
 */
public final class MetaBuilder {
    private MetaBuilder() {
    }

    public static Map<String, Meta> fromClass(Class<?> modelClass) {
        if (modelClass == null) {
            throw new IllegalArgumentException("modelClass cannot be null");
        }
        if (modelClass.isRecord()) {
            return fromRecord(modelClass);
        }
        return fromFields(modelClass);
    }

    private static Map<String, Meta> fromRecord(Class<?> recordClass) {
        Map<String, Meta> metas = new LinkedHashMap<>();
        for (RecordComponent component : recordClass.getRecordComponents()) {
            String fieldName = component.getName();
            Class<?> fieldType = component.getType();
            MetaField annotation = component.getAnnotation(MetaField.class);
            metas.put(fieldName, buildMeta(fieldName, fieldType, annotation));
        }
        return metas;
    }


    private static Map<String, Meta> fromFields(Class<?> modelClass) {
        Map<String, Meta> metas = new LinkedHashMap<>();
        for (Field field : modelClass.getDeclaredFields()) {
            if (shouldIgnoreField(field)) {
                continue;
            }
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            MetaField annotation = field.getAnnotation(MetaField.class);
            metas.put(fieldName, buildMeta(fieldName, fieldType, annotation));
        };
        return metas;
    }

    private static boolean shouldIgnoreField(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic();
    }

    private static Meta buildMeta(String fieldName, Class<?> fieldType, MetaField annotation) {
        MetaType type = resolveType(fieldType);
        Object defaultValue = resolveDefaultValue(fieldName, annotation);
        List<String> values = resolveValues(fieldType, annotation);
        String libelle = resolveLibelle(annotation);
        return new Meta(
                type,
                (List<String>) defaultValue,
                values,
                libelle
        );
    }

    private static MetaType resolveType(Class<?> fieldType) {
        if (fieldType.isEnum()) {
            return MetaType.TYPE_SELECT;
        } else if (isDateType(fieldType)) {
            return MetaType.DATE;
        }
        return MetaType.INPUT;
    }

    private static String resolveLibelle(MetaField annotation) {
        if (annotation != null) {
            return annotation.libelle();
        }
        return "";
    }

    private static List<String> resolveValues(Class<?> fieldType, MetaField annotation) {
        if  (fieldType.isEnum()) {
            return Arrays.stream(fieldType.getEnumConstants())
                    .map(value -> ((Enum<?>)value).name())
                    .toList();
        }
        if (annotation != null && annotation.values().length > 0) {
            return Arrays.asList(annotation.values());
        }
        return null;
    }

    private static Object resolveDefaultValue(String fieldName, MetaField annotation) {
        if (annotation != null) {
            return annotation.defaultvalue();
        }
        return null;
    }

    private static boolean isDateType(Class<?> fieldType) {
        return Date.class.isAssignableFrom(fieldType) || LocalDate.class.isAssignableFrom(fieldType) || LocalDateTime.class.isAssignableFrom(fieldType);
    }
}
