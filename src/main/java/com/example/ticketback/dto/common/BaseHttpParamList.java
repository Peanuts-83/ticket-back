package com.example.ticketback.dto.common;

/**
 * Paramètres génériques pour les appels de liste
 * @param pageNum
 * @param nb nombre de lignes
 */
public record BaseHttpParamList(
        Integer pageNum,
        Integer nb
) {

    public static BaseHttpParamList defaultValue() {
        return new BaseHttpParamList(0, 30);
    }

    /**
     * @return la page demandée ou 0
     */
    public int resolvedPageNum() {
        return pageNum != null ? pageNum : 0;
    }

    /**
     * @return nombre d'éléments ou 30 par défaut
     */
    public int resolvedNb() {
        return nb != null ? nb : 30;
    }
}
