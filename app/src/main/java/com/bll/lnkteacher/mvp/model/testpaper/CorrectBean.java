package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;
import java.util.List;

public class CorrectBean implements Serializable {

    public int id;
    public int type;//考试 类型班群单考、校群联考、际群联考
    public String title;
    public long time;
    public int sendStatus;//2为已批改完成
    public List<CorrectClassBean> examList;
    public int subType;
    public String taskName;
}
