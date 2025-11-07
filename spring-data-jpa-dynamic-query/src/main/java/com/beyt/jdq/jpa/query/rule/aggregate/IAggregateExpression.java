package com.beyt.jdq.jpa.query.rule.aggregate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * Interface for aggregate expressions (COUNT, SUM, AVG, MAX, MIN, etc.)
 * Used in SELECT and HAVING clauses
 */
public interface IAggregateExpression {
    /**
     * Generate aggregate expression using CriteriaBuilder
     * @param builder CriteriaBuilder instance
     * @param expression Expression to aggregate
     * @return Aggregated expression
     */
    Expression<?> generate(CriteriaBuilder builder, Expression<?> expression);
    
    /**
     * Get the aggregate type
     * @return AggregateType enum value
     */
    AggregateType getType();
}
