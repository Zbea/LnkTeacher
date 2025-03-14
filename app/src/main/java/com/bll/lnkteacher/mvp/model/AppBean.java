package com.bll.lnkteacher.mvp.model;

import android.graphics.drawable.Drawable;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppBean {
    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String appName;
    public String packageName;
    public byte[] imageByte;
    public boolean isTool;
    public Long time;
    @Transient
    public boolean isCheck;
    @Generated(hash = 558694803)
    public AppBean(Long id, long userId, String appName, String packageName, byte[] imageByte,
            boolean isTool, Long time) {
        this.id = id;
        this.userId = userId;
        this.appName = appName;
        this.packageName = packageName;
        this.imageByte = imageByte;
        this.isTool = isTool;
        this.time = time;
    }
    @Generated(hash = 285800313)
    public AppBean() {
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
    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public byte[] getImageByte() {
        return this.imageByte;
    }
    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }
    public boolean getIsTool() {
        return this.isTool;
    }
    public void setIsTool(boolean isTool) {
        this.isTool = isTool;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }

}
