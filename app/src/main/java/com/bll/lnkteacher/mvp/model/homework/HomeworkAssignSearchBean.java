package com.bll.lnkteacher.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HomeworkAssignSearchBean {

    public int taskId;
    public String title;
    @SerializedName("layoutTime")
    public long time;//预设时间
    public String answerUrl;
    public String examUrl;

    public int commonTypeId;
    public String name;
    public int subType;
    public int grade;
    public String lastConfig;
    public List<Integer> classIds=new ArrayList<>();
    public int addType;

    public boolean isCheck;

}
