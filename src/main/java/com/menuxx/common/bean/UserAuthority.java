package com.menuxx.common.bean;

public class UserAuthority {

    private Integer id;

    private Integer authorityId;

    private Authority authority;

    private Integer userId;

    public UserAuthority() {
    }

    public UserAuthority(Integer authorityId, Integer userId) {
        this.authorityId = authorityId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}