package com.example.ticketback.dto.common;

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
public record HttpPostPayload<T>(
        BaseHttpParams params,
        T data
) {
}
