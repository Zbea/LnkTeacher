package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.DecimalFormat

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_code,getClassNumStr(item.classNum))
            setText(R.id.tv_job,DataBeanManager.groupJobs[item.job])
            setText(R.id.tv_number,item.studentCount.toString())
            setVisible(R.id.tv_send,item.job==1)

            addOnClickListener(R.id.tv_info,R.id.tv_dissolve,R.id.tv_edit,R.id.tv_send)
        }
    }

    private fun getClassNumStr(num:Int):String{
        return DecimalFormat("000000").format(num)
    }

}
