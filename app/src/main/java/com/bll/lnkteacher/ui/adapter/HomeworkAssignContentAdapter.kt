package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkAssignContent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignContentAdapter(layoutResId: Int, data: List<HomeworkAssignContent>?) : BaseQuickAdapter<HomeworkAssignContent, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HomeworkAssignContent) {
        helper.setText(R.id.cb_check,item.name)
        helper.setOnCheckedChangeListener(R.id.cb_check) { p0, p1 ->
            item.isCheck=p1
        }
    }

}
