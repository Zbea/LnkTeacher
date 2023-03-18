package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_code,item.classNum.toString())
            setText(R.id.tv_date,DateUtils.longToStringDataNoHour(item.time))
            setText(R.id.tv_number,item.studentCount.toString())
            setText(R.id.tv_course,item.subject)

            addOnClickListener(R.id.ll_content,R.id.tv_info,R.id.tv_dissolve,R.id.tv_edit)
        }
    }

}
