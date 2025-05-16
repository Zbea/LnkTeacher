package com.bll.lnkteacher.mvp.model.testpaper;

import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
        @SerializedName("minute")
        public int standardTime;//标准时间
        public String url;
        public String answerUrl;
        public String examUrl;
//        public String question;
        public int questionType;
        public boolean isCheck;
        public List<AutoAssignItem> sysTaskList;
        public List<HomeworkAssignItem> assignItems=new ArrayList<>();
        public HomeworkAssignItem assignItem;
    }

    public static class AutoAssignItem{
        public String data;
    }
}
