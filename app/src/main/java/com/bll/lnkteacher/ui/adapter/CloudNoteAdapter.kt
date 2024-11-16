package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CloudNoteAdapter(layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {
        helper.setText(R.id.tv_title,"(${item.typeStr})"+item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoHour(item.date))
        helper.addOnClickListener(R.id.iv_delete)
    }

}
