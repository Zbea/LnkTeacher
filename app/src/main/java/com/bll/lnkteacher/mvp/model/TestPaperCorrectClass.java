package com.bll.lnkteacher.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * 考卷学生试卷提交列表
 */
public class TestPaperCorrectClass implements Serializable {

    public int total;
    public int doneNumber;
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
        public int status;
        public int score;
        public int sendStatus;
        public int examChangeId;
        public int[] ids;
        @SerializedName("Type")
        public int type;
        public String name;
        public boolean isCheck;
    }
}
