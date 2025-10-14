package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.Authorization;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Authorization entity.
 */
@Repository
public interface MongoAuthorizationRepository extends JpaDynamicQueryMongoRepository<Authorization, Long> {
}


