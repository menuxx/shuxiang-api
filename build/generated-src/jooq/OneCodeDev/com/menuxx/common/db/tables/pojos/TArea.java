/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;

import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TArea implements Serializable {

    private static final long serialVersionUID = 650035823;

    private final UInteger id;
    private final Integer  type;
    private final String   name;
    private final Integer  pid;
    private final Integer  sortWeight;
    private final String   postalCode;

    public TArea(TArea value) {
        this.id = value.id;
        this.type = value.type;
        this.name = value.name;
        this.pid = value.pid;
        this.sortWeight = value.sortWeight;
        this.postalCode = value.postalCode;
    }

    public TArea(
        UInteger id,
        Integer  type,
        String   name,
        Integer  pid,
        Integer  sortWeight,
        String   postalCode
    ) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.pid = pid;
        this.sortWeight = sortWeight;
        this.postalCode = postalCode;
    }

    public UInteger getId() {
        return this.id;
    }

    public Integer getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPid() {
        return this.pid;
    }

    public Integer getSortWeight() {
        return this.sortWeight;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TArea (");

        sb.append(id);
        sb.append(", ").append(type);
        sb.append(", ").append(name);
        sb.append(", ").append(pid);
        sb.append(", ").append(sortWeight);
        sb.append(", ").append(postalCode);

        sb.append(")");
        return sb.toString();
    }
}
