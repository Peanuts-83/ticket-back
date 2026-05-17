package com.example.ticketback.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload générique des endpoints
 *
 * @example create
 {
 *   "data": {
 *     "username": "admin",
 *     "email": "admin@ticketflow.local",
 *     "password": "password"
 *   }
 * }
 *
 * @example update
 * {
 *   "data": {
 *     "id": 1,
 *     "username": "admin",
 *     "email": "admin@ticketflow.local"
 *   }
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
