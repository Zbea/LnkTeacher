package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目分数
 */
public class ScoreItem implements Serializable {
    public double score=0;
    public int sort;
    public int result=0;//0错1对
    public double label;//题目标准分数
    public List<ScoreItem> childScores=new ArrayList<>();
}
