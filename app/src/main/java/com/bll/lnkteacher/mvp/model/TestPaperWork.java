package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;
import java.util.List;

public class TestPaperWork implements Serializable {

    public int id;
    public String type;//考试 类型
    public int typeId;
    public String testPaperType;//考试内容分组
    public int testPaperTypeId;
    public long commitDate;
    public long createDate;
    public int state;//0未批改
    public List<ListBean> lists;
    public String[] images = {
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
            , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
    };

    public class ListBean implements Serializable{

        public int id;
        public String className;
        public int classId;
        public int number;//班级人数
        public int receiveNumber;//提交的人数
        public int state;//是否批改
    }

}
