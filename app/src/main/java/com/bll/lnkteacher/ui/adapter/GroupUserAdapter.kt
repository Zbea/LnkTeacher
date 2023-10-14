package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class GroupUserAdapter(layoutResId: Int, data: List<GroupUser>?) : BaseQuickAdapter<GroupUser, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: GroupUser) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_number, ToolUtils.getFormatNum(item.classNum,"000000"))
        helper.setText(R.id.tv_school,item.school)
        helper.setText(R.id.tv_address,item.studentCount.toString()+"人")
    }



}
