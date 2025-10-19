package com.beyt.jdq.jpa.query.rule.specification;


import com.beyt.jdq.core.model.Criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Created by tdilber at 25-Aug-19
 */
public interface ISpecificationFilterRule {
    Predicate generatePredicate(Path<?> root, CriteriaBuilder builder, Criteria criteria);
}
