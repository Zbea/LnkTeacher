package com.bll.lnkteacher.mvp.model.testpaper;

import java.util.ArrayList;
import java.util.List;

/**
 * 考卷分类
 */
public class TestPaperType {

    public int total;
    public List<TypeBean> list=new ArrayList();

    public static class TypeBean {
        public int id;
        public String name;
        public int type;//1考卷2作业本
        public int grade;
    }
}
