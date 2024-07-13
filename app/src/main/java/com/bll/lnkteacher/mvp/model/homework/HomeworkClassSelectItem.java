package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;

/**
 * 选择班级信息
 */
public class HomeworkClassSelectItem implements Serializable {
    public int classId;
    public String className;
    public boolean isCheck;
    public boolean isCommit;
    public long date;

}
