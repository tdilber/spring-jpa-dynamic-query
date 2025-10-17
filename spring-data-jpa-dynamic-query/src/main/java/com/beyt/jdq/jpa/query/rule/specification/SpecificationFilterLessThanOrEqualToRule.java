package com.beyt.jdq.jpa.query.rule.specification;

import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.util.SpecificationUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Created by tdilber at 25-Aug-19
 */
public class SpecificationFilterLessThanOrEqualToRule implements ISpecificationFilterRule {

    @Override
    public Predicate generatePredicate(Path<?> root, CriteriaBuilder builder, Criteria criteria) {
        Predicate predicate;
        SpecificationUtil.checkHasFirstValue(criteria);
        predicate = builder.lessThanOrEqualTo(root.<Comparable>get(criteria.getKey()), (Comparable) criteria.getValues().get(0));

        return predicate;
    }
}
