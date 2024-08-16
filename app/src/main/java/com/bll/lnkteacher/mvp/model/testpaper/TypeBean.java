package com.bll.lnkteacher.mvp.model.testpaper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 作业 、考卷分类内容
 */
public class TypeBean implements Serializable {
    public int id;
    public String name;
    public int subType;//作业  2作业本 1 作业卷 3朗读本4题卷本
    public int grade;
    public int type;//1考卷2作业本
    public int bookId;
    public String bgResId;

    public String classIds;
}
