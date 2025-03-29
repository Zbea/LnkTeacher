package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.CalenderItemBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CalenderListAdapter(layoutResId: Int, data: List<CalenderItemBean>?) : BaseQuickAdapter<CalenderItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CalenderItemBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,getView(R.id.iv_image),8)
            setText(R.id.tv_buy,if (item.buyStatus==1) "下载" else "购买")
            addOnClickListener(R.id.tv_buy)
        }
    }

}
