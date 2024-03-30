package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamCorrectAdapter(layoutResId: Int, data: List<ExamCorrectList.ExamCorrectBean>?) : BaseQuickAdapter<ExamCorrectList.ExamCorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamCorrectList.ExamCorrectBean) {
        helper.setText(R.id.tv_exam,item.examName)
        helper.setText(R.id.tv_class_name,item.className)
        helper.setText(R.id.tv_num,"${item.submitCount}人")
        helper.addOnClickListener(R.id.tv_save)
    }

}
