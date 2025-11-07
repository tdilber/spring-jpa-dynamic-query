package com.beyt.jdq.jpa.query.rule.aggregate;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory to create aggregate expression instances
 */
public class AggregateExpressionFactory {
    
    private static final Map<AggregateType, IAggregateExpression> EXPRESSION_MAP = new HashMap<>();
    
    static {
        EXPRESSION_MAP.put(AggregateType.COUNT, new CountExpression());
        EXPRESSION_MAP.put(AggregateType.COUNT_DISTINCT, new CountDistinctExpression());
        EXPRESSION_MAP.put(AggregateType.SUM, new SumExpression());
        EXPRESSION_MAP.put(AggregateType.AVG, new AvgExpression());
        EXPRESSION_MAP.put(AggregateType.MAX, new MaxExpression());
        EXPRESSION_MAP.put(AggregateType.MIN, new MinExpression());
    }
    
    /**
     * Get aggregate expression by type
     * @param type AggregateType
     * @return IAggregateExpression instance
     */
    public static IAggregateExpression getExpression(AggregateType type) {
        return EXPRESSION_MAP.get(type);
    }
}

