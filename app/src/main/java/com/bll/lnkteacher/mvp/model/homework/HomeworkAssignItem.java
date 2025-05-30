package com.bll.lnkteacher.mvp.model.homework;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择班级信息
 */
public class HomeworkAssignItem implements Serializable {
    public String contentStr;
    public int selfBatchStatus;//1自批
    @SerializedName("layoutTime")
    public long assignTime;
    public long endTime;
    public int showStatus;//0提交1不提交
    public int taskState;//2开启1关闭
    public List<Integer> classIds=new ArrayList<>();
}
