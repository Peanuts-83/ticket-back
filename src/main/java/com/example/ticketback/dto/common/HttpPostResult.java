package com.example.ticketback.dto.common;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Réponse générique de l'api
 * @param data données retournées,
 * @param metas métadonnées techniques des champs
 * @param nb nb de lignes retournées dans data pour les listes
 */
public record HttpPostResult<T>(
        T data,
        Map<String, Meta> metas,
        Long nb
) {
    /**
     * Réponse avec data
     */
    public static <T> HttpPostResult<T> of(T data) {
        return new HttpPostResult<>(data, null, null);
    }

    /**
     * Réponse avec data + nb pour une liste
     */
    public static <T> HttpPostResult<T> ofList(T data, Long nb) {
        return new HttpPostResult<>(data, null, nb);
    }

    /**
     * Réponse avec métadonnées générées automatiquement
     */
    public static <T> HttpPostResult<T> ofMeta(@NonNull T data) {
        return new HttpPostResult<>(data, MetaBuilder.fromClass(data.getClass()), null);
    }

    /**
     * Réponse avec métadonnées générées via une classe fournie manuellement
     */
    public static <T> HttpPostResult<T> ofMeta(@NonNull T data, @NonNull Class<?> modelClass) {
        return new HttpPostResult<>(data, MetaBuilder.fromClass(modelClass), null);
    }

    /**
     * Réponse avec métadonnées fournies manuellement pour des cas complexes
     */
    public static <T> HttpPostResult<T> ofMeta(@NonNull T data, @NonNull Map<String, Meta> modelClass) {
        return new HttpPostResult<>(data, modelClass, null);
    }

}
