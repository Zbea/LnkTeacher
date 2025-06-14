package com.bll.lnkteacher.mvp.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Objects;

public class ItemList implements Serializable ,Comparable<ItemList>{

    public int type;
    public String desc;

    public int id;
    public String name;

    public Drawable icon; //1
    public Drawable icon_check; //2 选中的状态

    public int page;//目录页码

    public boolean isCheck;
    public String url;
    public int resId;
    public boolean isEdit;
    public boolean isAdd;

    public ItemList() {
    }

    public ItemList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(ItemList itemList) {
        return this.id-itemList.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemList item = (ItemList) obj;
        return Objects.equals(desc, item.desc) && type==item.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(desc, type);
    }
}
