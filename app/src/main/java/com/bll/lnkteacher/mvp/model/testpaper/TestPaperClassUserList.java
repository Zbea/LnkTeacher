package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * 考卷学生试卷提交列表
 */
public class TestPaperClassUserList implements Serializable {

    public int total;
    public int totalSend;
    public int totalSubmit;
    public int totalUpdate;
    public List<UserBean> list;

    public static class UserBean {
        public int studentTaskId;
        public int userId;
        public String createTime;
        public int classId;
        @SerializedName("taskImageId")
        public String imageUrl;//原图地址
        public String submitUrl;
        public String studentUrl;
        public int taskId;
        public int status;//3学生未提交1已提交未批改2已批改
        public int score;
        public int sendStatus;
        public int examChangeId;
        public int[] ids;
        @SerializedName("Type")
        public int type;
        public String name;
        public boolean isCheck;
        public int questionType;
        public String question;//题目数据
    }
}
