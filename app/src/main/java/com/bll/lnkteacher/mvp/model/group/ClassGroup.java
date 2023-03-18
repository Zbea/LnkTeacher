package com.bll.lnkteacher.mvp.model.group;

import com.bll.lnkteacher.mvp.model.User;

import java.io.Serializable;
import java.util.List;

public class ClassGroup implements Serializable {

    public int classId;
    public String name;//班群名
    public int classNum;//群号
    public long time;//创建时间
    public int studentCount;//人数
    public List<User> users;
    public String subject;
    public int subjectId;
    public String school;
    public String province;//省
    public int provinceId;
    public String city;
    public int cityId;

    public boolean isCheck;


}
