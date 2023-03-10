package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.TestPaperGrade
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperGradeAdapter(layoutResId: Int, data: List<TestPaperGrade>?) : BaseQuickAdapter<TestPaperGrade, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaperGrade) {
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_score,item.score.toString())
        helper.setText(R.id.tv_rank,(helper.adapterPosition+1).toString())
        helper.setText(R.id.tv_class_name,item.className)
    }

}
