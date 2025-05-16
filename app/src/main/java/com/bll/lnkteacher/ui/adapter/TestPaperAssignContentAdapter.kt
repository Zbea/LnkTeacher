package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAssignContentAdapter(layoutResId: Int, data: List<AssignPaperContentList.AssignPaperContentBean>?) : BaseQuickAdapter<AssignPaperContentList.AssignPaperContentBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AssignPaperContentList.AssignPaperContentBean) {
        helper.apply {
            setChecked(R.id.cb_check,item.isCheck)
            setText(R.id.cb_check,"  "+item.title)
            setGone(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            GlideUtils.setImageRoundUrl(mContext,item.url,getView(R.id.iv_image),8)
            addOnClickListener(R.id.cb_check,R.id.tv_answer,R.id.iv_image)
            addOnLongClickListener(R.id.iv_image)
        }
    }
}
