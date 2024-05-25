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
        val isHeader=mUserId==item.userId
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_number,item.studentCount.toString()+"人")
            setText(R.id.tv_code,if (item.state==1)ToolUtils.getFormatNum(item.classGroupId,"000000") else "")
            setText(R.id.tv_teacher,item.teacher)
            setText(R.id.tv_dissolve,if (isHeader)"解散" else "退出")
            setText(R.id.tv_course,if (item.state!=1)"" else if (isHeader) "课程表" else "查看")
            setText(R.id.tv_edit,if (isHeader)"修改" else  "")
            setText(R.id.tv_child,if (item.state==1)"子群创建" else  "子群管理")
            setVisible(R.id.tv_child,isHeader)
            addOnClickListener(R.id.tv_dissolve,R.id.tv_course,R.id.tv_edit,R.id.tv_child,R.id.tv_detail)
        }
    }
}
