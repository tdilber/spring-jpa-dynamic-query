package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * COUNT DISTINCT aggregate expression
 * Counts the number of unique results
 */
public class CountDistinctExpression implements IAggregateExpression {
    
    @Override
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.countDistinct(expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.COUNT_DISTINCT;
    }
}

