package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.group.ClassGroupTeacher
import com.bll.lnkteacher.utils.SPUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupTeacherAdapter(layoutResId: Int,private val isCreate:Boolean, data: List<ClassGroupTeacher>?) : BaseQuickAdapter<ClassGroupTeacher, BaseViewHolder>(layoutResId, data) {
    private val mUserId=SPUtil.getObj("user", User::class.java)?.accountId
    override fun convert(helper: BaseViewHolder, item: ClassGroupTeacher) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_phone,item.phone)
            setText(R.id.tv_subject,item.subject)
            setVisible(R.id.tv_transfer,isCreate&&item.userId!=mUserId)
            setVisible(R.id.tv_out,isCreate&&item.userId!=mUserId)
            addOnClickListener(R.id.tv_out,R.id.tv_transfer)
        }
    }



}
