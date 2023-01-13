package com.bll.lnkteacher.mvp.model;

import java.util.List;

public class HomeworkAssign {

    public String content;
    public long date;
    public String homeworkType;
    public int homeworkTypeId;
    public List<ClassGroupBean> lists;

    public class ClassGroupBean {
        public String classId;
        public String className;
        public boolean isCheck;
        public boolean isCommit;
        public long date=0;
    }

}
