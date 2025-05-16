package com.bll.lnkteacher.ui.adapter

import android.util.Log
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TabClassAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
//        Log.d(Constants.DEBUG,"dddddddddddddddd")
        helper.setText(R.id.tv_name,item.name)
        val tvText=helper.getView<TextView>(R.id.tv_name)
//        tvText.isSelected=item.isCheck
        if (item.isCheck){
            tvText.setTextColor(mContext.resources.getColor(R.color.black))
//            tvText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }
        else{
            tvText.setTextColor(mContext.resources.getColor(R.color.black_90))
//            tvText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        }
    }
}
