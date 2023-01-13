package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AppBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.setText(R.id.tv_name,item.appName)
        helper.setImageDrawable(R.id.iv_image,item.image)

        helper.addOnClickListener(R.id.iv_image)

    }



}
