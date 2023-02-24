package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;

public class ListItem implements Serializable {

    public int id;
    public String name;
    public int page;//目录页码
    public boolean isCheck;

    public ListItem() {
    }

    public ListItem(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
