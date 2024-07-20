package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择班级信息
 */
public class HomeworkClassSelectItem implements Serializable {
    public boolean isCorrect;//是否批改
    public boolean isCommit;//是否提交
    public long commitDate;
    public List<Integer> classIds=new ArrayList<>();
}
