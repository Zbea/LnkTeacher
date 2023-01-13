package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Notebook
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteAdapter(layoutResId: Int, data: List<Notebook>?) : BaseQuickAdapter<Notebook, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Notebook) {
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date, DateUtils.longToStringDataNoYearNoHour(item.createDate))
        helper.addOnClickListener(R.id.iv_edit)
        helper.addOnClickListener(R.id.iv_delete)
    }

}
