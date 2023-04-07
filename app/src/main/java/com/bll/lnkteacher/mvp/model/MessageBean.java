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
    public boolean isCheck;

}
