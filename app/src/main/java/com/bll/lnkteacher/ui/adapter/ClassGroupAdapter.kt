package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_code,ToolUtils.getFormatNum(item.classNum,"000000"))
            setText(R.id.tv_job, DataBeanManager.groupJobs[item.job-1])
            setText(R.id.tv_number,item.studentCount.toString())
            addOnClickListener(R.id.tv_info,R.id.tv_dissolve,R.id.tv_edit,R.id.tv_send)
        }
    }
}
