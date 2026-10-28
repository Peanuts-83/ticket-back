package com.example.ticketback.dto.common.paramlist;

import com.example.ticketback.dto.common.paramlist.constants.OrderWay;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParamSort(
        @Schema(description = "Champ de la table") String fieldName,
        @Schema(description = "Sens du tri")OrderWay orderWay
        ) {
}
