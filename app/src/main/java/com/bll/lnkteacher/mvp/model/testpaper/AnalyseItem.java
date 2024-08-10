package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据分析
 */
public class AnalyseItem implements Serializable {
    public int sort;
    public int num;//统计人数
    public int rightNum;//正确人数
    public int wrongNum;//错误人数
    public double totalScore;//统计总分
    public int totalLabel;//每题标准总分
    public double averageScore;//统计平均分
    public double scoreRate;//得分率
    public List<UserBean> rightStudents=new ArrayList<>();//题目正确学生
    public List<UserBean> wrongStudents=new ArrayList<>();//题目错误学生
    public List<AnalyseItem> childAnalyses=new ArrayList<>();

    public static class UserBean{
        public int userId;
        public String name;
        public int score;

        public UserBean(int userId,String name,int score){
            this.userId=userId;
            this.name=name;
            this.score=score;
        }
    }
}
