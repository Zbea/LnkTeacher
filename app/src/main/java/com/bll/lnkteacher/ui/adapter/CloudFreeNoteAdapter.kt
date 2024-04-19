package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.FreeNoteBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudFreeNoteAdapter(layoutResId: Int, data: List<FreeNoteBean>?) : BaseQuickAdapter<FreeNoteBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: FreeNoteBean) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringWeek1(item.date))
        helper.addOnClickListener(R.id.iv_delete)
    }

}
