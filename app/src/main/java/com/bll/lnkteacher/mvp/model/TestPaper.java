package com.bll.lnkteacher.mvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TestPaper {

    public int total;
    public List<ListBean> list=new ArrayList<>();

    public class ListBean{
        public int taskId;
        public int taskImageId;
        public String createTme;
        public int type;
        public int userId;
        public int commonTypeId;
        @SerializedName("name")
        public String typeName;
        public String title;
        @SerializedName("Urls")
        public List<String> urls;
        @SerializedName("FileNames")
        public List<String> fileNames;
        public String url;
        public boolean isCheck;
    }
}
