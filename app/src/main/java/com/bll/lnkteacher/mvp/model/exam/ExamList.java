package com.bll.lnkteacher.mvp.model.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExamList implements Serializable {
    public int total;
    public List<ExamBean> list;

    public class ExamBean implements Serializable{
        public List<ExamClassBean> classList;
        public String examName;
        public int id;
        public String createTime;
        public String examUrl;//考试试卷
        public String answerUrl;//答案
        public String question;
        public int questionType;//-1未加入模板0空模板
        public int questionMode;//1打分 2对错
    }

    public class ExamClassBean implements Serializable{
        public int classId;
        public int studentCount;
        public int schoolExamJobId;
        public String className;
        public int sendCount;
    }
}
