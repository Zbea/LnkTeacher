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
            setVisible(R.id.tv_edit,isHeader)
            setText(R.id.tv_child,if (item.state==1)"层群创建" else  "层群编辑")
            setGone(R.id.tv_code,item.state==1)
            setGone(R.id.tv_teacher,item.state==1)
            setGone(R.id.tv_course,item.state==1)
            setGone(R.id.iv_space,item.state==1)
            addOnClickListener(R.id.tv_dissolve,R.id.tv_course,R.id.tv_edit,R.id.tv_child,R.id.tv_detail)
        }
    }
}
