package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignSearchBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignSearchAdapter(layoutResId: Int, data: List<HomeworkAssignSearchBean>?) : BaseQuickAdapter<HomeworkAssignSearchBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkAssignSearchBean) {
        helper.apply {
            setText(R.id.tv_type,item.name)
            setText(R.id.tv_name,item.title)
            setText(R.id.tv_date_preset,DateUtils.longToHour(item.time))
            setText(R.id.tv_standard_time,if (item.standardTime>0) "${item.standardTime}分钟" else "")
            setGone(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            setChecked(R.id.cb_check,item.isCheck)
            addOnClickListener(R.id.tv_name,R.id.tv_answer,R.id.cb_check)
        }
    }

}
