package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

//课程表
@Entity
public class ClassScheduleBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public int type;//1排课表2课程表
    public int classGroupId;//班群id
    @Unique
    public int viewId;//对应textview  ID
    public String name;//科目名称
    public int mode;//五天六节课类型

    @Generated(hash = 892259453)
    public ClassScheduleBean(Long id, long userId, int type, int classGroupId, int viewId, String name,
            int mode) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.classGroupId = classGroupId;
        this.viewId = viewId;
        this.name = name;
        this.mode = mode;
    }
    @Generated(hash = 112340798)
    public ClassScheduleBean() {
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
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getClassGroupId() {
        return this.classGroupId;
    }
    public void setClassGroupId(int classGroupId) {
        this.classGroupId = classGroupId;
    }
    public int getViewId() {
        return this.viewId;
    }
    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMode() {
        return this.mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

}
