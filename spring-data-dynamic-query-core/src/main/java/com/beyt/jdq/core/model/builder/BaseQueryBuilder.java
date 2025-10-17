package com.beyt.jdq.core.model.builder;

import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.core.model.builder.interfaces.*;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseQueryBuilder<T, ID> implements DistinctWhereOrderByPage<T, ID>, WhereOrderByPage<T, ID>, OrderByPage<T, ID>, PageableResult<T, ID>, Result<T, ID> {
    protected final DynamicQuery dynamicQuery;

    public BaseQueryBuilder() {

        dynamicQuery = new DynamicQuery();
    }

    public DistinctWhereOrderByPage<T, ID> select(QuerySimplifier.SelectRule... selectRules) {
        dynamicQuery.getSelect().addAll(Arrays.stream(selectRules).map(o -> Pair.of(o.getFieldName(), o.getAlias())).collect(Collectors.toList()));
        return this;
    }

    public WhereOrderByPage<T, ID> distinct(boolean distinct) {
        dynamicQuery.setDistinct(distinct);
        return this;
    }


    public OrderByPage<T, ID> where(Criteria... criteria) {
        dynamicQuery.getWhere().addAll(Arrays.asList(criteria));
        return this;
    }


    public PageableResult<T, ID> orderBy(QuerySimplifier.OrderByRule... pairs) {
        dynamicQuery.getOrderBy().addAll(Arrays.stream(pairs).map(o -> Pair.of(o.getFieldName(), o.getOrderType())).collect(Collectors.toList()));
        return this;
    }

    public Result<T, ID> page(int pageNumber, int pageSize) {
        dynamicQuery.setPageSize(pageSize);
        dynamicQuery.setPageNumber(pageNumber);
        return this;
    }

    public abstract List<T> getResult();

    public abstract <ResultValue> List<ResultValue> getResult(Class<ResultValue> resultValueClass);

    public abstract Page<T> getResultAsPage();

    public abstract <ResultValue> Page<ResultValue> getResultAsPage(Class<ResultValue> resultValueClass);
}
