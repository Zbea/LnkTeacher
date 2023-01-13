package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Group
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class GroupAdapter(layoutResId: Int, data: List<Group>?) : BaseQuickAdapter<Group, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Group) {
        helper.setText(R.id.tv_name,item.schoolName)
        helper.setText(R.id.tv_teacher,item.teacherName)
        helper.setText(R.id.tv_date,DateUtils.longToStringDataNoHour(item.extraTime))
        helper.setText(R.id.tv_number,item.totalUser.toString()+"人")
        helper.setText(R.id.tv_out,if (item.selfStatus==1) "解散" else "退出")

        helper.addOnClickListener(R.id.tv_out,R.id.tv_details)
    }

}
