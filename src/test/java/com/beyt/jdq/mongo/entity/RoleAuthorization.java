package com.beyt.jdq.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * MongoDB RoleAuthorization entity for testing.
 * Junction entity connecting Roles and Authorizations.
 */
@Document(collection = "role_authorizations")
public class RoleAuthorization {
    
    @Id
    private Long id;
    
    private Long roleId;
    
    @DBRef
    private Authorization authorization;

    public RoleAuthorization() {
    }

    public RoleAuthorization(Long id, Long roleId, Authorization authorization) {
        this.id = id;
        this.roleId = roleId;
        this.authorization = authorization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleAuthorization)) return false;
        RoleAuthorization that = (RoleAuthorization) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RoleAuthorization{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", authorization=" + authorization +
                '}';
    }
}


