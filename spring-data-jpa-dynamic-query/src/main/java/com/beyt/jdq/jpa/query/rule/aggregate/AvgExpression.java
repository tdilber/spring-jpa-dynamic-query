package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * AVG aggregate expression
 * Calculates the average of a numeric field
 */
public class AvgExpression implements IAggregateExpression {
    
    @Override
    @SuppressWarnings("unchecked")
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.avg((Expression<Number>) expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.AVG;
    }
}

