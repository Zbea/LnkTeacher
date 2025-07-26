package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.ResultStandardItem.ResultChildItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicResultStandardAdapter(layoutResId: Int, data: List<ResultChildItem>?) : BaseQuickAdapter<ResultChildItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ResultChildItem) {
        helper.setText(R.id.tv_score,item.sortStr)
        helper.setImageResource(R.id.iv_result,if (item.isCheck) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
    }

}
