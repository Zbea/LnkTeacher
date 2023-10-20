package com.bll.lnkteacher.mvp.model.group;

import java.util.List;

public class Group {

    public int id;

    public String schoolName;

    public int userId;

    public String createTime;

    public int totalGroup;
    public String subject;
    public int grade;

    public int totalUser;

    public String teacherName;

    public String teacherSchool;

    public Long extraTime;

    public List<ClassGroup> classGroups;

    public int selfStatus;//1创建者，2加入者

}
