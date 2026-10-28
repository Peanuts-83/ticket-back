package com.example.ticketback.dto.common;


import com.example.ticketback.dto.common.paramlist.ParamFilter;
import com.example.ticketback.dto.common.paramlist.ParamSort;
import com.example.ticketback.dto.common.paramlist.constants.OrderWay;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

import java.util.List;


/**
 * Paramètres génériques pour les appels de liste
 *
 * @param pageNum
 * @param nb      nombre de lignes
 */
@Schema(description = "Http params pour une liste")
public record BaseHttpParamList(
        @Schema(description = "Numéro de page")
        Integer pageNum,
        @Schema(description = "Nombre d'items par page")
        Integer nb,
        @Schema(description = "filtres sur la requête", nullable = true)
        ParamFilter filters,
        @Schema(description = "Tri", nullable = true)
        List<ParamSort> sorts
) {

    public static BaseHttpParamList defaultValue() {
        return new BaseHttpParamList(0, 30, null, null);
    }

    /**
     * @return la page demandée ou 0
     * Front est 1-based / Back est 0-based
     */
    public int resolvedPageNum() {
        return pageNum != null ? pageNum - 1 : 0;
    }

    /**
     * @return nombre d'éléments ou 30 par défaut
     */
    public int resolvedNb() {
        return nb != null ? nb : 30;
    }

    public ParamFilter resolvedFilter() {
        return filters;
    }

    public Sort resolvedSort(Sort defaultSort) {
        if (sorts == null || sorts.isEmpty()) {
            return defaultSort;
        }
        return Sort.by(sorts.stream()
                .map(s -> new Sort.Order(
                        s.orderWay() == OrderWay.DESC ? Sort.Direction.DESC : Sort.Direction.ASC, s.fieldName()))
                .toList());

    }
}
