package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * COUNT aggregate expression
 * Counts the number of results
 */
public class CountExpression implements IAggregateExpression {
    
    @Override
    public Expression<?> generate(CriteriaBuilder builder, Expression<?> expression) {
        return builder.count(expression);
    }
    
    @Override
    public AggregateType getType() {
        return AggregateType.COUNT;
    }
}

