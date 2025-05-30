package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AccountQdBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AccountXdAdapter(layoutResId: Int, data: List<AccountQdBean>?) : BaseQuickAdapter<AccountQdBean, BaseViewHolder>(layoutResId, data) {

    var mPosition = 0

    override fun convert(helper: BaseViewHolder, item: AccountQdBean) {
        helper.setText(R.id.tv_name,item.amount.toString())
        if (helper.adapterPosition==mPosition){
            helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_black_solid_5dp_corner)
            helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.white) )
        }
        else{
            helper.setBackgroundRes(R.id.tv_name,R.drawable.bg_black_stroke_5dp_corner)
            helper.setTextColor(R.id.tv_name,mContext.resources.getColor(R.color.black))
        }
    }

    fun setItemView(position: Int) {
        mPosition=position
        notifyDataSetChanged()
    }

}
