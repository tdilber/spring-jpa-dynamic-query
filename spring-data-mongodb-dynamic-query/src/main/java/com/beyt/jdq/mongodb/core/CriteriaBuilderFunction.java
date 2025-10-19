package com.beyt.jdq.mongodb.core;

import com.beyt.jdq.core.model.DynamicQuery;
import org.springframework.data.mongodb.core.query.Criteria;


public interface CriteriaBuilderFunction {
    Criteria apply(com.beyt.jdq.core.model.Criteria searchCriteria, DynamicQuery searchQuery);
}
