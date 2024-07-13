package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAssignContentAdapter(layoutResId: Int, data: List<AssignPaperContentBean>?) : BaseQuickAdapter<AssignPaperContentBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AssignPaperContentBean) {
        helper.apply {
            setText(R.id.cb_check,"  "+item.title)
            setChecked(R.id.cb_check,item.isCheck)
            GlideUtils.setImageUrl(mContext,item.url,getView(R.id.iv_image))
            addOnClickListener(R.id.cb_check)
        }
    }

}
