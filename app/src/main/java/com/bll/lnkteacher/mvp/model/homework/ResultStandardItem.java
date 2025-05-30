package com.bll.lnkteacher.mvp.model.homework;

import java.util.List;

public class ResultStandardItem {
    public String title;
    public int sort;
    public double score;
    public List<ResultChildItem> list;

    public static class ResultChildItem{
        public int sort;
        public String sortStr;
        public double score;
        public boolean isCheck;
    }
}
