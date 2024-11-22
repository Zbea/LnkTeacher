package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultiScoreAdapter(layoutResId: Int, private val scoreMode:Int, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,ToolUtils.numbers[item.sort+1])
        helper.setText(R.id.tv_score,if (scoreMode==1) item.score.toString() else if (item.result==1)"对" else "错")
        helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
        helper.setGone(R.id.rv_list,!item.childScores.isNullOrEmpty())
        helper.setGone(R.id.iv_result,item.childScores.isNullOrEmpty())

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext,2)
        val mAdapter = ChildAdapter(R.layout.item_topic_score,scoreMode, item.childScores)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(helper.adapterPosition,view,position)
        }

        helper.addOnClickListener(R.id.tv_score,R.id.iv_result)
    }


    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int, view: View, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }


    class ChildAdapter(layoutResId: Int, private val scoreMode:Int, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort,"${item.sort+1}")
                helper.setText(R.id.tv_score,item.score.toString())
                helper.setText(R.id.tv_score,if (scoreMode==1) item.score.toString() else if (item.result==1)"对" else "错")
                helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_correct_right else R.mipmap.icon_correct_wrong)
                addOnClickListener(R.id.tv_score,R.id.iv_result)
            }
        }
    }

}
