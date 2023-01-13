package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;

public class ListBean implements Serializable {

    public int id;
    public String name;
    public int page;//目录页码
    public boolean isCheck;

    public ListBean() {
    }

    public ListBean(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
