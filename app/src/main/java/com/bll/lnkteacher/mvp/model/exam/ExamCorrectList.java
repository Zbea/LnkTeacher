package com.bll.lnkteacher.mvp.model.exam;

import java.util.List;

public class ExamCorrectList {

    public List<ExamCorrectBean> list;

    public static class ExamCorrectBean{
        public int id;
        public int userId;
        public int classId;
        public int schoolExamJobId;
        public String className;
        public String examName;
        public int submitCount;
        public int studentCount;
        public int questionType;
    }

}
