package com.beyt.jdq.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * MongoDB Authorization entity for testing.
 * Represents permissions/authorizations in the system.
 */
@Document(collection = "authorizations")
public class Authorization {
    
    @Id
    private Long id;
    
    private String name;
    
    private String menuUrl;
    
    private String menuIcon;

    public Authorization() {
    }

    public Authorization(Long id, String name, String menuUrl, String menuIcon) {
        this.id = id;
        this.name = name;
        this.menuUrl = menuUrl;
        this.menuIcon = menuIcon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authorization)) return false;
        Authorization that = (Authorization) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(name, that.name) && 
               Objects.equals(menuUrl, that.menuUrl) && 
               Objects.equals(menuIcon, that.menuIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, menuUrl, menuIcon);
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", menuIcon='" + menuIcon + '\'' +
                '}';
    }
}


