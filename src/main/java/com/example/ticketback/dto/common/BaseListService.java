package com.example.ticketback.dto.common;

import com.example.ticketback.dto.common.paramlist.ParamFilterTranslator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BaseListService<T,U> {
    protected final ParamFilterTranslator translator;

    protected abstract JpaSpecificationExecutor<T> executor();
    protected abstract Set<String> authorizedFields();
    protected abstract Sort sort();
    protected abstract U toDto(T entity);

    protected List<T> listWithFilters(@Nullable BaseHttpParams a_params) {
        BaseHttpParamList l_paramList = a_params != null ? a_params.resolvedparamList() : BaseHttpParamList.defaultValue();
        Pageable l_pageable = PageRequest.of(l_paramList.resolvedPageNum(), l_paramList.resolvedNb(), l_paramList.resolvedSort());
        Specification<T> l_spec = translator.toSpecification(l_paramList.filters(), authorizedFields());
        return (List<T>) executor().findAll(l_spec, l_pageable).stream().map(this::toDto).toList();
    }
}
