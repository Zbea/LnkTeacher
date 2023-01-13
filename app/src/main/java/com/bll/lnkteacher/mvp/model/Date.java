package com.bll.lnkteacher.mvp.model;


import com.bll.lnkteacher.utils.date.Lunar;
import com.bll.lnkteacher.utils.date.Solar;

import java.io.Serializable;
import java.util.List;

public class Date implements Serializable {

    public int year;
    public int month;
    public int day;
    public int week;//2星期一 8星期日
    public long time;
    public boolean isNow;//是否是当天
    public boolean isNowMonth;//是否是当月

    public Solar solar=new Solar();
    public Lunar lunar=new Lunar();

    public DateEvent dateEvent;


}
