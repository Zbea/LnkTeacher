package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Objects;

@Entity
public class DiaryBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
    public String title;
    public long date;
    public int size;
    public String bgRes;
    @Generated(hash = 1887232553)
    public DiaryBean(Long id, long userId, String title, long date, int size,
            String bgRes) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.size = size;
        this.bgRes = bgRes;
    }
    @Generated(hash = 1749744078)
    public DiaryBean() {
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getBgRes() {
        return this.bgRes;
    }
    public void setBgRes(String bgRes) {
        this.bgRes = bgRes;
    }
   
}
