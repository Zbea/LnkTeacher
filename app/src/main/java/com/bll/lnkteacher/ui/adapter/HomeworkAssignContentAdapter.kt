package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignContentAdapter(layoutResId: Int, data: List<AssignPaperContentList.AssignPaperContentBean>?) : BaseQuickAdapter<AssignPaperContentList.AssignPaperContentBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AssignPaperContentList.AssignPaperContentBean) {
        helper.apply {
            setText(R.id.cb_check,"  "+item.title)
            setChecked(R.id.cb_check,item.isCheck)
            setGone(R.id.ll_date_preset,DateUtils.date10ToDate13(item.time)>System.currentTimeMillis())
            setText(R.id.tv_date_preset,DateUtils.longToStringWeek(item.time))
            setVisible(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            addOnClickListener(R.id.cb_check,R.id.tv_answer,R.id.iv_delete)
        }
    }

}
