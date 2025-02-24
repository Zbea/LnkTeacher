package com.bll.lnkteacher.ui.adapter

import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, val scoreMode:Int, private val module:Int, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort+1] else "${item.sort+1}")
        helper.getView<TextView>(R.id.tv_sort).layoutParams.width=if (module==1) DP2PX.dip2px(mContext,55f) else DP2PX.dip2px(mContext,40f)
        helper.setText(R.id.tv_score,if (scoreMode==1)item.score.toString() else if (item.result==1)"对" else "错" )
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }
}
