package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
    private val mUserId= SPUtil.getObj("user", User::class.java)?.accountId
    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_number,item.studentCount.toString())
            if (item.state==1){
                setText(R.id.tv_code,ToolUtils.getFormatNum(item.classId,"000000"))
            }
            setText(R.id.tv_teacher,item.teacher)
            setText(R.id.tv_dissolve,if (item.state==1)"更多" else "解散")
            addOnClickListener(R.id.tv_info,R.id.tv_dissolve)
        }
    }
}
