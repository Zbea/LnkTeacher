package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteAdapter(layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoYearNoHour(item.date))
        helper.addOnClickListener(R.id.iv_edit)
        helper.addOnClickListener(R.id.iv_delete)
    }

}
