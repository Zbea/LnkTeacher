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
        helper.setGone(R.id.iv_password,item.isSet&&item.typeStr==mContext.getString(R.string.note_tab_diary))
        if (item.isSet)
            helper.setImageResource(R.id.iv_password,if (item.isCancelPassword) R.mipmap.icon_encrypt else R.mipmap.icon_encrypt_check)
        helper.addOnClickListener(R.id.iv_edit,R.id.iv_password,R.id.iv_delete)
    }

}
