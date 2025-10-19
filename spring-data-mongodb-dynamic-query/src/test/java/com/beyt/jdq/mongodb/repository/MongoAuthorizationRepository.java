package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Authorization;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Authorization entity.
 */
@Repository
public interface MongoAuthorizationRepository extends MongoDynamicQueryRepository<Authorization, Long> {
}


