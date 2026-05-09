package com.example.ticketback.dto.common;

/**
 * Réponse générique de l'api
 * @param data données retournées,
 * @param nb   nb de lignes pour les listes
 */
public record HttpPostResult<T>(
        T data,
        Long nb
) {
    /**
     * Réponse avec data
     */
    public static <T> HttpPostResult<T> of(T data) {
        return new HttpPostResult<T>(data, null);
    }

    /**
     * Réponse avec data + nb pour une liste
     */
    public static <T> HttpPostResult<T> of(T data, Long nb) {
        return new HttpPostResult<T>(data, nb);
    }
}
