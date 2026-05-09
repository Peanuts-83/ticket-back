package com.example.ticketback.dto.common;

/**
 * Paramètres génériques des endpoints
 * @param id         clé du bean,
 * @param dataType   Type d'écran,
 * @param routeParam Id de l'api,
 * @param paramList  Paramètres de liste
 */
public record BaseHttpParams(
        String id,
        ViewDataType dataType,
        Long routeParam,
        BaseHttpParamList paramList
) {

    public BaseHttpParamList resolvedparamList() {
        return paramList != null ? paramList : BaseHttpParamList.defaultValue();
    }
}
