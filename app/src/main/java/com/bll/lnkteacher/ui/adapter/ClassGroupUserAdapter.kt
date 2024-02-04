package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupUserAdapter(layoutResId: Int,private val type: Int, private val isHeadTeacher:Boolean, data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
        helper.apply {
            setText(R.id.tv_name,item.nickname)
            setText(R.id.tv_birthday,DateUtils.longToStringDataNoHour(item.birthdayTime*1000))
            setText(R.id.tv_parent,item.parentName)
            setText(R.id.tv_parent_name,item.parentNickname)
            setText(R.id.tv_phone,item.parentTel)
            setText(R.id.tv_address,item.parentAddr)
            setText(R.id.tv_job,item.job)

            setGone(R.id.tv_out,isHeadTeacher)
            setVisible(R.id.iv_check,type==1)
            setImageResource(R.id.iv_check,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)

            addOnClickListener(R.id.tv_out,R.id.tv_job,R.id.iv_check)
        }
    }



}
