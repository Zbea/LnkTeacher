package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.HandoutDaoManager
import com.bll.lnkteacher.mvp.model.HandoutBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HandoutsAdapter(layoutResId: Int, data: List<HandoutBean>?) : BaseQuickAdapter<HandoutBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HandoutBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            setVisible(R.id.iv_download, HandoutDaoManager.getInstance().isExist(item.id))
        }
    }

}
