package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.BaseTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<BaseTypeBean>?) : BaseQuickAdapter<BaseTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BaseTypeBean) {
        helper.setText(R.id.tv_name,item.name)
        helper.addOnClickListener(R.id.iv_edit)
        helper.addOnClickListener(R.id.iv_delete)
        helper.addOnClickListener(R.id.iv_top)
    }

}
