package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkCorrectContent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkWorkNameAdapter(layoutResId: Int, data: List<HomeworkCorrectContent.UserBean>?) : BaseQuickAdapter<HomeworkCorrectContent.UserBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkCorrectContent.UserBean) {
        helper.setText(R.id.tv_name,item.name)
    }

}
