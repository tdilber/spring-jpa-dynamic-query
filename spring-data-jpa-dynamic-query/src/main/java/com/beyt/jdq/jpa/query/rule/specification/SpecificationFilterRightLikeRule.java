package com.beyt.jdq.jpa.query.rule.specification;

import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.exception.DynamicQueryNoAvailableValueException;
import com.beyt.jdq.core.util.SpecificationUtil;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Created by tdilber at 25-Aug-19
 */
public class SpecificationFilterRightLikeRule implements ISpecificationFilterRule {

    @Override
    public Predicate generatePredicate(Path<?> root, CriteriaBuilder builder, Criteria criteria) {
        SpecificationUtil.checkHasFirstValue(criteria);
        Predicate[] predicates = new Predicate[criteria.getValues().size()];
        for (int i = 0; i < criteria.getValues().size(); i++) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                predicates[i] = builder.like(root.<String>get(criteria.getKey()), criteria.getValues().get(i) + "%");
            } else {
                throw new DynamicQueryNoAvailableValueException("Need String Type: " + criteria.getKey());
            }
        }

        return builder.or(predicates);
    }
}
