package com.bll.lnkteacher.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 讲义
 */
public class HandoutList {

    public int total;
    public List<HandoutBean> list;

    public static class HandoutBean{
        public int id;
        public String title;
        @SerializedName("url")
        public String imageUrl;
        @SerializedName("paths")
        public String bodyUrl;
        public String grade;
        public String bookPath;
        public String bookDrawPath;
    }

}
