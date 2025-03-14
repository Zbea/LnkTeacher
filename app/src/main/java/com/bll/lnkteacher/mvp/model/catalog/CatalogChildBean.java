package com.bll.lnkteacher.mvp.model.catalog;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class CatalogChildBean implements Serializable, MultiItemEntity {

    public String title;
    public int pageNumber;
    public String picName;
    public int parentPosition;

    @Override
    public int getItemType() {
        return 1;
    }
}
