package com.bll.lnkteacher.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeworkType implements Serializable {

    public int total;
    public List<TypeBean> list=new ArrayList();

    public static class TypeBean implements Serializable {
        public int taskId;
        public int commonTypeId;
        public String title;
        public String name;
        public int subType;//2作业本 1 作业卷 3朗读本
        public int grade;
    }

}
