package com.bll.lnkteacher.mvp.model.homework;

import com.bll.lnkteacher.MethodManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class HomeworkContentBean implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public long date;//创建时间
    public String title;
    public int typeId;
    public int contentId;
    public String filePath;//文件路径
    public String mergePath;//合图路径

    @Generated(hash = 1207323603)
    public HomeworkContentBean(Long id, long userId, long date, String title,
            int typeId, int contentId, String filePath, String mergePath) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.title = title;
        this.typeId = typeId;
        this.contentId = contentId;
        this.filePath = filePath;
        this.mergePath = mergePath;
    }
    @Generated(hash = 1693358578)
    public HomeworkContentBean() {
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
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getMergePath() {
        return this.mergePath;
    }
    public void setMergePath(String mergePath) {
        this.mergePath = mergePath;
    }
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

}
