package com.bll.lnkteacher.mvp.model.exam;

import com.bll.lnkteacher.mvp.model.HandoutsList;
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean;
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean;

import java.util.List;

public class ExamList {
    public int total;
    public List<ExamBean> list;

    public class ExamBean{
        public int id;
        public long time;
        public String examName;
        public List<CorrectClassBean> examList;
    }
}
