package com.example.ticketback.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload générique des endpoints
 *
 * @example create d'un utilisateur
 {
 *  "params": {
 *       "dataType": "create",
 *       "routeParam": null,
 *       "paramList": null
 *   },
 *   "data": {
 *     "username": "admin",
 *     "email": "admin@ticketflow.local",
 *     "password": "password"
 *   }
 * }
 *
 * @example update d'un utilisateur
 * {
 *  "params": {
 *       "dataType": "update",
 *       "routeParam": 5,
 *       "paramList": null
 *   },
 *   "data": {
 *     "email": "admin@ticketflow.local"
 *   }
 * }
 *
 * @example liste des tickets "DONE" par position ascendante
 * {
 *     "params": {
 *         "dataType": "liste",
 *         "routeParam": null,
 *         "paramList": {
 *             "pageNum": 1,
 *             "nb": 10,
 *             "filters": {
 *                 "fieldName": "status",
 *                 "fieldOperator": "EQUAL",
 *                 "value": "DONE"
 *             },
 *             "sort": [
 *                  {
 *                      field: "position",
 *                      way: "ASC"
 *                  }
 *             ]
 *         }
 *     }
 * }

 * @param params BaseHttpParams
 * @param data T
 */
@Schema(description = "Payload attendu d'une requête")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HttpPostPayload<T>(
        @Schema(description = "HttpParams de toute requête")
        BaseHttpParams params,
        @Schema(description = "Données T [key: value]")
        T data
) {
}
