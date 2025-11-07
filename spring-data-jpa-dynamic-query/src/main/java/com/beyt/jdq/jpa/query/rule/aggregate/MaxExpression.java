package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * MAX aggregate expression
 * Finds the maximum value
 */
public class MaxExpression implements IAggregateExpression {
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.max((Expression) expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.MAX;
    }
}

