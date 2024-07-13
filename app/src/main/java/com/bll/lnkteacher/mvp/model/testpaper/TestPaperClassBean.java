package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestPaperClassBean implements Serializable {
    public int classId;
    public int classGroupId;
    @SerializedName("jobImage")
    public String imageUrl;//原图
    public String answerUrl;//答案图
    public String name;//班级名称
    public String examName;//考试名称
    public int examChangeId;
    public int status;
    public long endTime;
    public int totalStudent;
    public int totalSubmitStudent;
    public int totalSend;
    public int grade;
}
