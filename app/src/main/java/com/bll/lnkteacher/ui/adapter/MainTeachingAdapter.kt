package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainTeachingAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)

        val dateEvent=DateEventDaoManager.getInstance().queryBean(item.classId,DateUtils.getStartOfDayInMillis())
        if (dateEvent!=null)
            helper.setText(R.id.tv_plan,dateEvent.content)
    }
}
