package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    private var module=0
    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort] else item.sort.toString())
        helper.setText(R.id.tv_score,item.score)
//        helper.setVisible(R.id.iv_tips,item.isCheck)
        helper.addOnClickListener(R.id.tv_score)
    }

    fun setChangeModule(type:Int){
        module=type
        notifyDataSetChanged()
    }

}
