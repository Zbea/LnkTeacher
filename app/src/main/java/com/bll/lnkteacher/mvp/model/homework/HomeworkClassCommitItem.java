package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;

//**提交班级信息
public class HomeworkClassCommitItem implements Serializable {
    public int classId;
    public long endTime;
    public int submitStatus;//1不提交0提交
    public String className;
    public long time;
}
