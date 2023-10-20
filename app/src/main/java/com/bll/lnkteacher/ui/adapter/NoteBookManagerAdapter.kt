package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NoteBookManagerAdapter(layoutResId: Int, data: List<ItemTypeBean>?) : BaseQuickAdapter<ItemTypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemTypeBean) {
        helper.setText(R.id.tv_name,item.title)
        helper.addOnClickListener(R.id.iv_edit,R.id.iv_delete,R.id.iv_top)
    }

}
