package com.bll.lnkteacher.mvp.model;

import com.bll.lnkteacher.mvp.model.group.ClassGroup;

import java.util.List;

public class MessageBean {

    public int id;
    public String content;
    public long createTime;
    public boolean isCheck;
    public List<ClassGroup> classGroups;


}
