package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamScoreItem implements Serializable {
    public String score;
    public int sort;
    public boolean isCheck;
    public List<ExamScoreItem> childScores=new ArrayList<>();
}
