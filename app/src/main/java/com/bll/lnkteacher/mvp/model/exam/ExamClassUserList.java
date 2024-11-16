package com.bll.lnkteacher.mvp.model.exam;

import java.io.Serializable;
import java.util.List;


public class ExamClassUserList implements Serializable {
    public List<ClassUserBean> list;

    public static class ClassUserBean {
        public int id;
        public int schoolExamJobId;
        public int userId;
        public int classId;
        public String teacherUrl;
        public String studentUrl;
        public int status;//3学生未提交1已提交未批改2已批改
        public double score;
        public String studentName;
        public boolean isCheck;
        public String question;//题目数据
        public int questionType;
    }
}
