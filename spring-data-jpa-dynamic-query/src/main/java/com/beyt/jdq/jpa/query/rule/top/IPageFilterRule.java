package com.beyt.jdq.jpa.query.rule.top;

import org.springframework.data.domain.Pageable;

/**
 * Created by tdilber at 28-Aug-19
 */
public interface IPageFilterRule {
    Pageable generatePageRequest(int page, int size, String field);
}
