package com.beyt.jdq.mongo;

import com.beyt.jdq.dto.DynamicQuery;
import org.springframework.data.mongodb.core.query.Criteria;


public interface CriteriaBuilderFunction {
    Criteria apply(com.beyt.jdq.dto.Criteria searchCriteria, DynamicQuery searchQuery);
}
