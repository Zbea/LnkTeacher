package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAssignContentAdapter(layoutResId: Int, data: List<AssignPaperContentList.AssignPaperContentBean>?) : BaseQuickAdapter<AssignPaperContentList.AssignPaperContentBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AssignPaperContentList.AssignPaperContentBean) {
        helper.apply {
            setText(R.id.cb_check,"  "+item.title)
            setChecked(R.id.cb_check,item.isCheck)
            GlideUtils.setImageUrl(mContext,item.url,getView(R.id.iv_image))
            setVisible(R.id.ll_date_preset,DateUtils.date10ToDate13(item.time)>System.currentTimeMillis())
            setText(R.id.tv_date_preset,DateUtils.longToStringWeek(item.time))
            setGone(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            addOnClickListener(R.id.cb_check,R.id.tv_answer)
        }
    }

}
