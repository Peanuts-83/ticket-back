package com.example.ticketback.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Paramètres génériques des endpoints
 * @param id         clé du bean,
 * @param dataType   Type d'écran,
 * @param routeParam Id de l'api,
 * @param paramList  Paramètres de liste
 */
@Schema(description = "HttpParams pour toute requête")
public record BaseHttpParams(
        @Schema(description = "Identifiant du bean <String>")
        String id,
        @Schema(description = "type d'écran demandé")
        ViewDataType dataType,
        @Schema(description = "id de référence <Long>")
        Long routeParam,
        @Schema(description = "Paramètres de liste si besoin")
        BaseHttpParamList paramList
) {

    public BaseHttpParamList resolvedparamList() {
        return paramList != null ? paramList : BaseHttpParamList.defaultValue();
    }
}
