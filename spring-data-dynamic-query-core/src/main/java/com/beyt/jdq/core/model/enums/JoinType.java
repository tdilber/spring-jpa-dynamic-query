package com.beyt.jdq.core.model.enums;

import java.io.Serializable;

/**
 * Created by tdilber at 7/13/2020
 */
public enum JoinType implements Serializable {
    INNER('.', jakarta.persistence.criteria.JoinType.INNER),
    LEFT('<', jakarta.persistence.criteria.JoinType.LEFT),
    RIGHT('>', jakarta.persistence.criteria.JoinType.RIGHT);

    private Character separator;
    private jakarta.persistence.criteria.JoinType joinType;

    JoinType(Character separator, jakarta.persistence.criteria.JoinType joinType) {
        this.separator = separator;
        this.joinType = joinType;
    }

    public Character getSeparator() {
        return separator;
    }

    public jakarta.persistence.criteria.JoinType getJoinType() {
        return joinType;
    }
}
