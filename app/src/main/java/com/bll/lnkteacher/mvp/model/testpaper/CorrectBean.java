package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CorrectBean implements Serializable {

    public int id;
    public String title;
    public String createTime;
    public long endTime;
    public int taskType;//1作业 2测试卷
    public String examUrl;//原图
    public String answerUrl;//答案
    public int subType;//作业分类
    @SerializedName("taskName")
    public String subTypeName;//作业分类名称
    public String question;
    public int questionType;//-1未加入模板0空模板
    public int questionMode;//1打分
    public int selfBatchStatus;//1自批
    public List<TestPaperClassBean> examList;

}
