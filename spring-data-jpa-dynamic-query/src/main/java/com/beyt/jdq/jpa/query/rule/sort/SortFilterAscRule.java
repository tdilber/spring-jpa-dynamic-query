package com.beyt.jdq.jpa.query.rule.sort;

import org.springframework.data.domain.Sort;

/**
 * Created by tdilber at 28-Aug-19
 */
public class SortFilterAscRule implements ISortFilterRule {

    @Override
    public Sort getSortFilterRule(String field) {
        return Sort.by(Sort.Direction.ASC, field);
    }
}
