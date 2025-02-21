package com.bll.lnkteacher.mvp.model.homework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择班级信息
 */
public class HomeworkClassSelectItem implements Serializable {
    public int selfBatchStatus;//1自批
    public boolean isCommit;
    public long commitDate;
    public List<Integer> classIds=new ArrayList<>();
}
