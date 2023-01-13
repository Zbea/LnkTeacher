package com.bll.lnkteacher.mvp.model;

import java.util.List;

public class HomeworkWorkContent {

    public int id;
    public String className;
    public int classId;
    public String homeworkType;
    public int homeworkTypeId;
    public long date;
    public String message;
    public List<UserBean> lists;
    public String[] images = {
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
            , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
    };

    public class UserBean {

        public int id;
        public String name;//提交作业的人名
        public int state;//作业批改状态
        public String score;
        public String[] images = {
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ffile1.renrendoc.com%2Ffileroot_temp2%2F2020-9%2F18%2F1c04fc93-c130-4779-8c4f-718922afd68e%2F1c04fc93-c130-4779-8c4f-718922afd68e1.gif&refer=http%3A%2F%2Ffile1.renrendoc.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659079134&t=aea0e93799e11e4154452df47c03f710"
                , "http://files.eduuu.com/img/2012/12/14/165129_50cae891a6231.jpg"
        };

    }

}
