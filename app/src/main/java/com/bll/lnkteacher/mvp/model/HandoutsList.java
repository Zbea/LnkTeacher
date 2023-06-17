package com.bll.lnkteacher.mvp.model;

import java.util.List;

/**
 * 讲义
 */
public class HandoutsList {

    public int total;
    public List<HandoutsBean> list;

    public class HandoutsBean{
        public int id;
        public String title;
        public String url;
        public String paths;
        public int grade;
    }

}
