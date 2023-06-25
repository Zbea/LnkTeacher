package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class Notebook {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("userTeacher", User.class)).accountId;
    public int typeId;
    public String title;
    public long date;
    @Generated(hash = 270609839)
    public Notebook(Long id, long userId, int typeId, String title, long date) {
        this.id = id;
        this.userId = userId;
        this.typeId = typeId;
        this.title = title;
        this.date = date;
    }
    @Generated(hash = 1348176405)
    public Notebook() {
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
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
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
   
}
