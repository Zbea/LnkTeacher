package com.bll.lnkteacher.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.ceil

class SpaceGridItemDeco2(private val width: Int, private val height: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)+1//当前位置
        val total=state.itemCount
        val line = ceil(total.toDouble() / 2).toInt()//最后一行
        val currentLine=ceil(position.toDouble() / 2).toInt()//当前行
        if (currentLine!=line){
            outRect.bottom = height
        }
        //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.left = 0
            outRect.right = width
        } else {
            outRect.left = width
            outRect.right = 0
        }
    }

}