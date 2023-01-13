package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ClassGroupUser
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupUserAdapter(layoutResId: Int, data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
        helper.setText(R.id.tv_name,item.nickname)
        helper.setText(R.id.tv_birthday,DateUtils.longToStringDataNoHour(item.birthdayTime*1000))
        helper.setText(R.id.tv_parent,item.parentName)
        helper.setText(R.id.tv_parent_name,item.parentNickname)
        helper.setText(R.id.tv_phone,item.parentTel)
        helper.setText(R.id.tv_address,item.parentAddr)
        helper.setText(R.id.tv_job,item.job)
        helper.setImageResource(R.id.iv_check,if (item.status==1) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)

        helper.addOnClickListener(R.id.tv_out,R.id.tv_job)
    }



}
