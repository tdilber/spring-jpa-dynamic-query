package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * SUM aggregate expression
 * Calculates the sum of a numeric field
 */
public class SumExpression implements IAggregateExpression {
    
    @Override
    @SuppressWarnings("unchecked")
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.sum((Expression<Number>) expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.SUM;
    }
}

