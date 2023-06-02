package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HandoutsList
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HandoutsAdapter(layoutResId: Int, data: List<HandoutsList.HandoutsBean>?) : BaseQuickAdapter<HandoutsList.HandoutsBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: HandoutsList.HandoutsBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            GlideUtils.setImageUrl(mContext,item.url,getView(R.id.iv_image))
        }
    }

}
