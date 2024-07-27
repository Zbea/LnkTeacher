package com.bll.lnkteacher.ui.adapter

import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamAnalyseAdapter(layoutResId: Int,private val module:Int, data: List<AnalyseItem>?) : BaseQuickAdapter<AnalyseItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AnalyseItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort+1] else "${item.sort+1}")
        helper.getView<TextView>(R.id.tv_sort).layoutParams.width=if (module==1) DP2PX.dip2px(mContext,45f) else DP2PX.dip2px(mContext,30f)
        helper.setText(R.id.tv_score,item.averageScore.toString())
        helper.setText(R.id.tv_wrong_num,item.wrongNum.toString())
        helper.addOnClickListener(R.id.tv_wrong_num)
    }
}
