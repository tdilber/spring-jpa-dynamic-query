package com.beyt.jdq.core.model.enums;

import java.io.Serializable;

/**
 * Created by tdilber at 7/13/2020
 */
public enum JoinType implements Serializable {
    INNER('.', javax.persistence.criteria.JoinType.INNER),
    LEFT('<', javax.persistence.criteria.JoinType.LEFT),
    RIGHT('>', javax.persistence.criteria.JoinType.RIGHT);

    private Character separator;
    private javax.persistence.criteria.JoinType joinType;

    JoinType(Character separator, javax.persistence.criteria.JoinType joinType) {
        this.separator = separator;
        this.joinType = joinType;
    }

    public Character getSeparator() {
        return separator;
    }

    public javax.persistence.criteria.JoinType getJoinType() {
        return joinType;
    }
}
