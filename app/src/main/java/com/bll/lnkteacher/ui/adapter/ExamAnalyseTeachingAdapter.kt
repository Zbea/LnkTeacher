package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem.UserBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamAnalyseTeachingAdapter(layoutResId: Int, data: List<UserBean>?) : BaseQuickAdapter<UserBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: UserBean) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_score,item.score.toString()+"分")
    }
}
