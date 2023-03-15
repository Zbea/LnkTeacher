package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 考卷批改列表
 */
public class TestPaperCorrect implements Serializable {

    public int total;
    public List<CorrectBean> list=new ArrayList<>();

    public static class CorrectBean implements Serializable{

        public int id;
        public int type;//考试 类型班群单考、校群联考、际群联考
        public String name;
        public int groupId;//群id
        public long time;
        public int sendStatus;//2为已批改完成
        public List<ClassBean> examList;
    }

    public static class ClassBean implements Serializable{
        public int taskId;
        public int classId;
        public String name;
        public String examName;
        public int examChangeId;
        public int status;
        public int totalStudent;
        public int totalSubmitStudent;
    }

}
