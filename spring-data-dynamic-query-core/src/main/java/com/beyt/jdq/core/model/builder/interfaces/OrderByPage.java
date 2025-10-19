package com.beyt.jdq.core.model.builder.interfaces;

import com.beyt.jdq.core.model.builder.QuerySimplifier;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderByPage<T, ID> {
    PageableResult<T, ID> orderBy(QuerySimplifier.OrderByRule... pairs);

    Result<T, ID> page(int pageNumber, int pageSize);

    List<T> getResult();

    Page<T> getResultAsPage();

    <ResultValue> List<ResultValue> getResult(Class<ResultValue> resultValueClass);

    <ResultValue> Page<ResultValue> getResultAsPage(Class<ResultValue> resultValueClass);
}
