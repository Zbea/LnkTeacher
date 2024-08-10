package com.bll.lnkteacher.mvp.model.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExamList implements Serializable {
    public int total;
    public List<ExamBean> list;

    public static class ExamBean implements Serializable{
        public List<ExamClassBean> classList;
        public String examName;
        public int id;
        public long endTime;
        public String createTime;
        public String examUrl;//考试试卷
        public String answerUrl;//答案
        public String question;
        public int questionType;//-1未加入模板0空模板
        public int questionMode;//1打分 2对错
        public int sendStatus;//2已发送
    }

    public static class ExamClassBean implements Serializable{
        public int classId;
        public int classGroupId;
        public int studentCount;
        public int schoolExamJobId;
        public String className;
        public int sendCount;
    }
}
