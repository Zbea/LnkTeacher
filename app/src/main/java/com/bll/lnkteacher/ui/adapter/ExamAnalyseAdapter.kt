package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamAnalyseAdapter(layoutResId: Int, data: List<AnalyseItem>?) : BaseQuickAdapter<AnalyseItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AnalyseItem) {
        helper.setText(R.id.tv_sort,item.sortStr)
        helper.setText(R.id.tv_score,item.averageScore.toString())
        helper.setText(R.id.tv_label_score,item.totalLabel.toString())
        helper.setText(R.id.tv_wrong_num,item.wrongNum.toString())
        helper.addOnClickListener(R.id.tv_wrong_num)
    }
}
