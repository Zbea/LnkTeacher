package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupChildUserAdapter(layoutResId: Int, private var isHeadTeacher:Boolean, data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
        helper.apply {
            setText(R.id.tv_name,item.nickname)

            setVisible(R.id.tv_out,isHeadTeacher)
            addOnClickListener(R.id.tv_out,R.id.tv_job)
        }
    }
}
