package com.bll.lnkteacher.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)
        val tvText=helper.getView<TextView>(R.id.tv_name)
        if (item.isCheck){
            tvText.setTextColor( mContext.getColor(R.color.black))
            tvText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            tvText.setBackgroundResource(R.drawable.bg_black_stroke_10dp_corner)
        }
        else{
            tvText.setTextColor( mContext.getColor(R.color.gray))
            tvText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            tvText.setBackgroundResource(R.drawable.bg_gray_stroke_10dp_corner)
        }
    }
}
