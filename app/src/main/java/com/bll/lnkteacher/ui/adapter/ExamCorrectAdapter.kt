package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.mvp.model.exam.ExamCorrectBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamCorrectAdapter(layoutResId: Int, data: List<ExamCorrectBean>?) : BaseQuickAdapter<ExamCorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamCorrectBean) {

    }

}
