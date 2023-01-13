package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignAdapter(layoutResId: Int, data: List<HomeworkType>?) : BaseQuickAdapter<HomeworkType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkType) {
        helper.setText(R.id.tv_name,item.name)
        helper.setImageResource(R.id.iv_image,item.resId)
    }

}
