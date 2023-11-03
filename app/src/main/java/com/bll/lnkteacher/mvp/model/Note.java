package com.bll.lnkteacher.mvp.model;


import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String title;
    public String typeStr;//笔记分类id
    public long date; //创建时间
    public String contentResId; //笔记内容背景id
    public boolean isCancelPassword;//取消加密
    @Transient
    public boolean isSet;
    @Transient
    public int cloudId;
    @Transient
    public String downloadUrl;
    @Transient
    public String contentJson;

    @Generated(hash = 150613865)
    public Note(Long id, long userId, String title, String typeStr, long date, String contentResId,
            boolean isCancelPassword) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.typeStr = typeStr;
        this.date = date;
        this.contentResId = contentResId;
        this.isCancelPassword = isCancelPassword;
    }
    @Generated(hash = 1272611929)
    public Note() {
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
    public String getTypeStr() {
        return this.typeStr;
    }
    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getContentResId() {
        return this.contentResId;
    }
    public void setContentResId(String contentResId) {
        this.contentResId = contentResId;
    }
    public boolean getIsCancelPassword() {
        return this.isCancelPassword;
    }
    public void setIsCancelPassword(boolean isCancelPassword) {
        this.isCancelPassword = isCancelPassword;
    }

}
