package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkWorkContent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkWorkNameAdapter(layoutResId: Int, data: List<HomeworkWorkContent.UserBean>?) : BaseQuickAdapter<HomeworkWorkContent.UserBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkWorkContent.UserBean) {
        helper.setText(R.id.tv_name,item.name)
    }

}
