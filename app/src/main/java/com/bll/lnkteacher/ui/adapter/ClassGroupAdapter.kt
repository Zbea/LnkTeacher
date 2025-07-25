package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        val isHeader=MethodManager.getAccountId()==item.userId
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_number,item.studentCount.toString()+"人")
            setText(R.id.tv_code,if (item.state==1)ToolUtils.getFormatNum(item.classGroupId,"000000") else "")
            setImageResource(R.id.iv_arrow_join,if(item.isAllowJoin==1)R.mipmap.icon_switch_true else R.mipmap.icon_switch_false)
            setVisible(R.id.iv_arrow_join,isHeader&&item.state==1)
            setVisible(R.id.tv_permission,item.state==1&&item.type==1)
            setText(R.id.tv_permission,if (item.permissionTime<System.currentTimeMillis())"权限*关" else "权限*开")
            setGone(R.id.tv_code,isHeader&&item.state==1)
            setText(R.id.tv_teacher,item.teacher)
            setGone(R.id.tv_teacher,item.state==1)
            setText(R.id.tv_dissolve,if (isHeader)"解散" else "退出")
            setVisible(R.id.tv_edit,isHeader)
            setText(R.id.tv_child,if (item.state==1)"层群创建" else  "层群编辑")
            setGone(R.id.iv_space,item.state==1)
            addOnClickListener(R.id.tv_dissolve,R.id.tv_edit,R.id.tv_child,R.id.tv_permission,R.id.iv_arrow_join)
        }
    }
}
