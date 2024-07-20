package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 作业、考卷布置内容
 */
public class AssignPaperContentList {

    public int total;
    public List<AssignPaperContentBean> list;

    public static class AssignPaperContentBean {
        public int taskId;
        public String title;
        @SerializedName("layoutTime")
        public long time;//预设时间
        public String url;
        public String answerUrl;
        public String examUrl;
        public String question;
        public int questionType;
        public boolean isCheck;
    }
}
