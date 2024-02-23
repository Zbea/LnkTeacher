package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.group.ClassGroupTeacher
import com.bll.lnkteacher.utils.SPUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupTeacherAdapter(layoutResId: Int, private var isCreate:Boolean, data: List<ClassGroupTeacher>?) : BaseQuickAdapter<ClassGroupTeacher, BaseViewHolder>(layoutResId, data) {
    private val mUserId=SPUtil.getObj("user", User::class.java)?.accountId
    override fun convert(helper: BaseViewHolder, item: ClassGroupTeacher) {
        helper.apply {
            setText(R.id.tv_name,item.name)
            setText(R.id.tv_phone,item.phone)
            setText(R.id.tv_subject,item.subject)
            if (!isCreate){
                setVisible(R.id.tv_transfer,false)
                setVisible(R.id.tv_out,false)
            }
            else{
                setVisible(R.id.tv_transfer,item.userId!=mUserId)
                setVisible(R.id.tv_out,item.userId!=mUserId)
            }
            addOnClickListener(R.id.tv_out,R.id.tv_transfer)
        }
    }


    fun setChange(boolean: Boolean){
        isCreate=boolean
        notifyDataSetChanged()
    }

}
