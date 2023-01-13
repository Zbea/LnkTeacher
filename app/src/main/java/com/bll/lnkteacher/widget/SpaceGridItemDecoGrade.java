package com.bll.lnkteacher.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceGridItemDecoGrade extends RecyclerView.ItemDecoration {

    private int width;
    private int height;

    public SpaceGridItemDecoGrade(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = width;
        outRect.bottom = height;
        //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) %2==0) {
            outRect.left = 0;
            outRect.right=width;
        }
        else {
            outRect.left = width;
            outRect.right=0;
        }
    }

}