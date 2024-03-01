package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;

public class CorrectClassBean implements Serializable {
    public int taskId;
    public int classId;
    public String name;
    public String examName;
    public int examChangeId;
    public int status;
    public long endTime;
    public int totalStudent;
    public int totalSubmitStudent;
    public int totalUpdate;
    public int totalSend;
    public int grade;
}
