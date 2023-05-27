package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.NoteTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<NoteTypeBean>?) : BaseQuickAdapter<NoteTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: NoteTypeBean) {
        helper.setText(R.id.tv_name,item.name)
        helper.addOnClickListener(R.id.iv_edit)
        helper.addOnClickListener(R.id.iv_delete)
        helper.addOnClickListener(R.id.iv_top)
    }

}
