package com.bll.lnkteacher.mvp.model.exam;

import java.io.Serializable;
import java.util.List;

public class ExamList implements Serializable {
    public int total;
    public List<ExamBean> list;

    public class ExamBean implements Serializable{
        public List<ClassBean> classList;
        public String examName;
        public int id;
        public String createTime;
        public String examUrl;//考试试卷
    }

    public class ClassBean implements Serializable{
        public int classId;
        public int studentCount;
        public int schoolExamJobId;
        public String className;
        public int sendCount;
    }
}
