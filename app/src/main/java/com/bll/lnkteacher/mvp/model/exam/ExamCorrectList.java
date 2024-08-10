package com.bll.lnkteacher.mvp.model.exam;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExamCorrectList {

    public List<ExamCorrectBean> list;

    public static class ExamCorrectBean implements Serializable {
        public int id;
        public int userId;
        public int classId;
        public int classGroupId;
        public int schoolExamJobId;
        public String className;
        public String examName;
        public int submitCount;
        public int studentCount;
        public String answerUrl;//答案
        public String question;
        public int questionType;//-1未加入模板0空模板
        public int questionMode;//1打分 2对错
    }

}
