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
            tvText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }
        else{
            tvText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        }
    }
}
