package com.beyt.jdq.core.model.builder.interfaces;

import org.springframework.data.domain.Page;

import java.util.List;

public interface PageableResult<T, ID> {
    Result<T, ID> page(int pageNumber, int pageSize);

    List<T> getResult();

    Page<T> getResultAsPage();

    <ResultValue> List<ResultValue> getResult(Class<ResultValue> resultValueClass);

    <ResultValue> Page<ResultValue> getResultAsPage(Class<ResultValue> resultValueClass);
}
