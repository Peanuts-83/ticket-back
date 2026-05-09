package com.example.ticketback.dto.common;

/**
 * Paramètres génériques pour les appels de liste
 * @param PageNum
 * @param nb nombre de lignes
 */
public record BaseHttpParamList(
        Integer PageNum,
        Integer nb
) {

    public static BaseHttpParamList defaultValue() {
        return new BaseHttpParamList(0, 30);
    }

    /**
     * @return la page demandée ou 0
     */
    public int resolvedPageNum() {
        return PageNum != null ? PageNum : 0;
    }

    /**
     * @return nombre d'éléments ou 30 par défaut
     */
    public int resolvedNb() {
        return nb != null ? nb : 30;
    }
}
