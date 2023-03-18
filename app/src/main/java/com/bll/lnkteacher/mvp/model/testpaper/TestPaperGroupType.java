package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 考卷布置 群分类
 */
public class TestPaperGroupType {

    private SubTypeBean subType;
    private List<TypeBean> type;

    public static class SubTypeBean {
        @SerializedName("1")
        public List<TypeBean> classGroups;
        @SerializedName("2")
        public List<TypeBean> schoolGroups;
        @SerializedName("3")
        public List<TypeBean> areaGroups;
    }

    public static class TypeBean{
        private int type;
        private String desc;
    }
}
