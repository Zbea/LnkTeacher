package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;

public class HomeworkClassSelect implements Serializable {
    public int classId;
    public long endTime;
    public int submitStatus;//1不提交0提交
    public String name;
    public long time;
}
