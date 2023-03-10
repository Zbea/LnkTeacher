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
        public int state;//0未批改
        public List<ClassBean> examList;
        public String[] images = {
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
                , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
        };
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
