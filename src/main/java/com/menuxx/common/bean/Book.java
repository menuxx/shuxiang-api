package com.menuxx.common.bean;

import com.menuxx.Const;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

public class Book {

    public static final String QINIU_PREFIX = "images/books/";

    private Integer id;

    private Integer groupId;

    private Group group;

    @NotEmpty
    private String name;

    private Integer price;

    @NotEmpty
    private String thumbImgs;

    @NotEmpty
    private String coverImage;

    private String coverImageUrl;

    @NotEmpty
    private String isbn;

    private String press;

    @NotEmpty
    private String authors;

    private String translators;

    private String describe;

    @NotEmpty
    private String shopUrl;

    private Date createAt;

    private Date updateAt;

    public String getCoverImageUrl() {
        return Const.CDNBaseUrl + QINIU_PREFIX + coverImage;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getThumbImgs() {
        return thumbImgs;
    }

    public void setThumbImgs(String thumbImgs) {
        this.thumbImgs = thumbImgs == null ? null : thumbImgs.trim();
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage == null ? null : coverImage.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press == null ? null : press.trim();
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors == null ? null : authors.trim();
    }

    public String getTranslators() {
        return translators;
    }

    public void setTranslators(String translators) {
        this.translators = translators == null ? null : translators.trim();
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl == null ? null : shopUrl.trim();
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}