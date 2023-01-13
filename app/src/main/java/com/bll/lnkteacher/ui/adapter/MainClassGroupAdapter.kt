package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class MainClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    private val nowDate = DateUtils.dateToStamp(SimpleDateFormat("yyyy-MM-dd").format(Date()))

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)

        val dateEvent=DateEventDaoManager.getInstance().queryBean(item.classId,nowDate)
        if (dateEvent!=null)
            helper.setText(R.id.tv_plan,dateEvent.content)
    }

}
