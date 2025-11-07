package com.beyt.jdq.core.model;

import com.beyt.jdq.core.model.enums.Order;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tdilber at 30-Dec-20
 */
public class DynamicQuery implements Serializable {

    protected boolean distinct = false;
    protected Integer pageSize = null;
    protected Integer pageNumber = null;
    protected List<Pair<String, String>> select = new ArrayList<>();
    protected List<Criteria> where = new CriteriaList();
    protected List<Pair<String, Order>> orderBy = new ArrayList<>();
    protected List<String> groupBy = new ArrayList<>();
    protected List<Criteria> having = new ArrayList<>();


    public static DynamicQuery of(List<Criteria> where) {
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getWhere().addAll(where);
        return dynamicQuery;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<Pair<String, String>> getSelect() {
        return select;
    }

    public void setSelect(List<Pair<String, String>> select) {
        this.select = select;
    }

    public List<Criteria> getWhere() {
        return where;
    }

    public void setWhere(List<Criteria> where) {
        this.where = where;
    }

    public List<Pair<String, Order>> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<Pair<String, Order>> orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    public List<Criteria> getHaving() {
        return having;
    }

    public void setHaving(List<Criteria> having) {
        this.having = having;
    }
}
