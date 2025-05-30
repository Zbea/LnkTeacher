package com.bll.lnkteacher.mvp.model.testpaper;

import java.io.Serializable;

public class TestPaperClassBean implements Serializable {
    public int classId;
    public int classGroupId;
    public String name;//班级名称
    public int status;
    public int totalStudent;
    public int totalSubmitStudent;
    public int totalUpdate;
}
