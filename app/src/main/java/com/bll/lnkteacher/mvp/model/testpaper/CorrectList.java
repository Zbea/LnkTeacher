package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 考卷批改列表
 */
public class CorrectList implements Serializable {

    public int total;
    public List<CorrectBean> list;
}
