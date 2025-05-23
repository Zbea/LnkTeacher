package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentTypeBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkDrawContentTypeAdapter(layoutResId: Int, data: List<HomeworkContentTypeBean>?) : BaseQuickAdapter<HomeworkContentTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkContentTypeBean) {
        helper.apply {
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date,DateUtils.longToStringDataNoYear(item.date))
            addOnClickListener(R.id.iv_delete)
        }
    }
}
