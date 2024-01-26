package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;

public class HomeworkClass implements Serializable {
    public int classId;
    public String className;
    public boolean isCheck;
    public boolean isCommit;
    public long date;
    public int submitStatus=1;
}
