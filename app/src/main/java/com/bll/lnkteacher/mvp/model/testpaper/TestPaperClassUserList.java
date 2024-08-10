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
    public List<ClassUserBean> list;
    public List<ClassUserBean> taskList;

    public static class ClassUserBean {
        public int studentTaskId;
        public int userId;
        public int classId;
        public String submitUrl;
        public String studentUrl;
        public int status;//3学生未提交1已提交未批改2已批改
        public int score;
        @SerializedName("Type")
        public int type;
        public String name;
        public boolean isCheck;
        public int questionType;
        public String question;//题目数据
    }
}
