package com.bll.lnkteacher.mvp.model;

import java.util.ArrayList;
import java.util.List;

public class TestPaperType {

    public int total;
    public List<TypeBean> list=new ArrayList();

    public static class TypeBean {
        public int id;
        public String name;
        public int type;//1考卷
    }
}
