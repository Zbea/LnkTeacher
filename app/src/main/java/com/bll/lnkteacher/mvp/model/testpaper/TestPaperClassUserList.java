package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * 考卷学生试卷提交列表
 */
public class TestPaperClassUserList implements Serializable {
    public List<ClassUserBean> taskList;

    public static class ClassUserBean {
        public int studentTaskId;
        public int userId;
        public int classId;
        public String submitUrl;
        public String studentUrl;
        public int status;//3学生未提交1已提交未批改2已批改
        public double score;
        public long takeTime;//用时
        @SerializedName("Type")
        public int type;
        public String name;
        public boolean isCheck;
        public int questionType;
        public String question;//题目数据
    }
}
