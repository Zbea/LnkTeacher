package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.mvp.model.group.ClassGroup;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageBean {

    public int id;
    @SerializedName("title")
    public String content;
    public long date;
    public String classInfo;
    public String teacherName;
    public int sendType;//1老师发 2学生发
    public boolean isCheck;

}
