package com.example.ticketback.dto.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;

/**
 * Annotation optionnelle pour enrichir les métadonnées d'un champ
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({RECORD_COMPONENT, FIELD})
public @interface MetaField {
    /**
     * Libellé à afficher
     */
    String libelle() default "";

    /**
     * Valeur par défaut pour un select/radioBtn ou autre
     */
    String defaultvalue() default "";

    /**
     * Valeurs possibles pour un select/radioBtn ou autre
     */
    String[] values() default {};
}
