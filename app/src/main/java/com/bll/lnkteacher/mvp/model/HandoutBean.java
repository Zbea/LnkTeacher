package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.utils.SPUtil;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HandoutBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String title;
    @SerializedName("url")
    public String imageUrl;
    @SerializedName("paths")
    public String bodyUrl;
    @SerializedName("grade")
    public String typeStr;
    public String bookPath;
    public String bookDrawPath;
    public long date;
    @Generated(hash = 55765589)
    public HandoutBean(Long id, long userId, String title, String imageUrl, String bodyUrl,
            String typeStr, String bookPath, String bookDrawPath, long date) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.bodyUrl = bodyUrl;
        this.typeStr = typeStr;
        this.bookPath = bookPath;
        this.bookDrawPath = bookDrawPath;
        this.date = date;
    }
    @Generated(hash = 23508257)
    public HandoutBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBodyUrl() {
        return this.bodyUrl;
    }
    public void setBodyUrl(String bodyUrl) {
        this.bodyUrl = bodyUrl;
    }
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public String getBookDrawPath() {
        return this.bookDrawPath;
    }
    public void setBookDrawPath(String bookDrawPath) {
        this.bookDrawPath = bookDrawPath;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }

   
}
