package com.beyt.jdq.jpa.query.rule.aggregate;

import java.io.Serializable;

/**
 * Enum representing different types of aggregate functions
 * Used in SELECT and HAVING clauses with syntax: [AggregateType]field.path
 * Example: [Count]user.address.city, [Sum]order.total, [Avg]product.price
 */
public enum AggregateType implements Serializable {
    COUNT("Count", "Counts the number of results"),
    COUNT_DISTINCT("CountDistinct", "Counts the number of unique results"),
    SUM("Sum", "Calculates the sum of a numeric field"),
    AVG("Avg", "Calculates the average of a numeric field"),
    MAX("Max", "Finds the maximum value"),
    MIN("Min", "Finds the minimum value");

    private final String prefix;
    private final String description;

    AggregateType(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Parse aggregate type from field path
     * @param fieldPath Field path that might contain aggregate prefix like [Count]field.name
     * @return AggregateType if found, null otherwise
     */
    public static AggregateType fromFieldPath(String fieldPath) {
        if (fieldPath == null || !fieldPath.trim().startsWith("[")) {
            return null;
        }

        fieldPath = fieldPath.trim();
        
        int endBracket = fieldPath.indexOf(']');
        if (endBracket == -1) {
            return null;
        }
        
        String prefix = fieldPath.substring(1, endBracket);
        
        for (AggregateType type : values()) {
            if (type.prefix.equalsIgnoreCase(prefix)) {
                return type;
            }
        }
        
        return null;
    }

    /**
     * Extract actual field path without aggregate prefix
     * @param fieldPath Field path like [Count]field.name
     * @return field.name
     */
    public static String extractFieldPath(String fieldPath) {
        if (fieldPath == null || !fieldPath.trim().startsWith("[")) {
            return fieldPath;
        }
        fieldPath = fieldPath.trim();
        
        int endBracket = fieldPath.indexOf(']');
        if (endBracket == -1) {
            return fieldPath;
        }
        
        return fieldPath.substring(endBracket + 1);
    }
}

