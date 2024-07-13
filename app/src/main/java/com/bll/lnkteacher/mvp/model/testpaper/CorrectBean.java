package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CorrectBean implements Serializable {

    public int id;
    public String title;
    public long time;
    public int sendStatus;//2为已批改完成
    @SerializedName("jobImage")
    public String imageUrl;//原图
    public List<TestPaperClassBean> examList;
    public int subType;//作业分类
    @SerializedName("taskName")
    public String subTypeName;//作业分类名称
    public String question;
    public int questionType;//-1未加入模板0空模板
}
