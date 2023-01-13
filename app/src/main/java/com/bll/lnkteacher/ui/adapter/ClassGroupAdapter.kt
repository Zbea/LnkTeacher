package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_code,item.classNum.toString())
        helper.setText(R.id.tv_date,DateUtils.longToStringDataNoHour(item.time))
        helper.setText(R.id.tv_number,item.studentCount.toString())
        helper.setText(R.id.tv_course,item.subject)

        helper.addOnClickListener(R.id.ll_content)
        helper.addOnClickListener(R.id.tv_info)
        helper.addOnClickListener(R.id.tv_dissolve)
        helper.addOnClickListener(R.id.tv_edit)
    }

}
