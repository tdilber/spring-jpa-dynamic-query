package com.beyt.jdq.core.model.builder.interfaces;

import org.springframework.data.domain.Page;

import java.util.List;

public interface Result<T, ID> {
    List<T> getResult();

    Page<T> getResultAsPage();

    <ResultValue> List<ResultValue> getResult(Class<ResultValue> resultValueClass);

    <ResultValue> Page<ResultValue> getResultAsPage(Class<ResultValue> resultValueClass);
}

