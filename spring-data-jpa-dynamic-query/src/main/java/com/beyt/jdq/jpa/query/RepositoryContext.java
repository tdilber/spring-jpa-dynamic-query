package com.beyt.jdq.jpa.query;

import com.beyt.jdq.core.deserializer.IDeserializer;
import jakarta.persistence.EntityManager;

/**
 * Context object that holds dependencies required for dynamic query execution
 */
public class RepositoryContext {
    private final EntityManager entityManager;
    private final IDeserializer deserializer;

    public RepositoryContext(EntityManager entityManager, IDeserializer deserializer) {
        this.entityManager = entityManager;
        this.deserializer = deserializer;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public IDeserializer getDeserializer() {
        return deserializer;
    }
}

