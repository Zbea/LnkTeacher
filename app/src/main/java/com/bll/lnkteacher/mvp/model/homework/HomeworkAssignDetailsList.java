package com.bll.lnkteacher.mvp.model.homework;

import java.util.ArrayList;
import java.util.List;

public class HomeworkAssignDetailsList {

    public int total;
    public List<DetailsBean> list=new ArrayList<>();

    public static class DetailsBean{
        public int id;
        public String info;
        public long time;
        public String name;
        public String classInfo;
        public int grade;
    }

}
