package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * MIN aggregate expression
 * Finds the minimum value
 */
public class MinExpression implements IAggregateExpression {
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.min((Expression) expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.MIN;
    }
}

