package com.beyt.jdq.jpa.query.rule.top;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by tdilber at 28-Aug-19
 */
public class TopFilterAscRule implements IPageFilterRule {

    @Override
    public Pageable generatePageRequest(int page, int size, String field) {
        return PageRequest.of(0, size, Sort.Direction.ASC, field);
    }
}
