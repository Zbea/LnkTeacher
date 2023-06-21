package com.bll.lnkteacher.mvp.model;

import java.io.Serializable;

public class ItemList implements Serializable {

    public int type;
    public String desc;

    public int id;
    public String name;
    public int page;//目录页码
    public boolean isCheck;

    public ItemList() {
    }

    public ItemList(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
