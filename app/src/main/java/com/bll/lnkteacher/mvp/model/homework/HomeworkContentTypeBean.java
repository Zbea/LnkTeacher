package com.bll.lnkteacher.mvp.model.homework;

import com.bll.lnkteacher.MethodManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class HomeworkContentTypeBean {

    @Unique
    @Id(autoincrement = true)
    public Long id;
    public long userId= MethodManager.getAccountId();
    public String title;
    public long date;
    public String path;
    public int typeId;
    public int contentId;

    @Generated(hash = 1373413654)
    public HomeworkContentTypeBean(Long id, long userId, String title, long date,
            String path, int typeId, int contentId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.path = path;
        this.typeId = typeId;
        this.contentId = contentId;
    }
    @Generated(hash = 2009361301)
    public HomeworkContentTypeBean() {
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
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getTypeId() {
        return this.typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    public int getContentId() {
        return this.contentId;
    }
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

}
