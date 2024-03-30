package com.bll.lnkteacher.mvp.model.exam;

import java.util.List;

public class ExamRankList{

    public List<ExamRankBean> list;
    public class ExamRankBean{
        public String className;
        public int classId;
        public String studentName;
        public int score;
    }
}
