package com.bll.lnkteacher.ui.adapter

import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicScoreAdapter(layoutResId: Int, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    private var module=0
    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,if (module==1) ToolUtils.numbers[item.sort] else item.sort.toString())
        helper.getView<TextView>(R.id.tv_sort).layoutParams.width=if (module==1) DP2PX.dip2px(mContext,45f) else DP2PX.dip2px(mContext,30f)
        helper.setText(R.id.tv_score,item.score)
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }

    fun setChangeModule(type:Int){
        module=type
        notifyDataSetChanged()
    }

}
