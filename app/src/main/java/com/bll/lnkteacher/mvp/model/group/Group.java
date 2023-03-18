package com.bll.lnkteacher.mvp.model.group;


import com.bll.lnkteacher.mvp.model.group.ClassGroup;

import java.util.List;

public class Group {

    public int id;

    public String schoolName;

    public int type;//1际群 2校群

    public int userId;

    public String createTime;

    public Object selClassList;

    public int totalGroup;

    public int totalUser;

    public String teacherName;

    public String teacherSchool;

    public Long extraTime;

    public List<ClassGroup> classGroups;

    public int selfStatus;//1创建者，2加入者

}
