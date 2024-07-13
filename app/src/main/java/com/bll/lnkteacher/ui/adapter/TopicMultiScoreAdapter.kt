package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultiScoreAdapter(layoutResId: Int, data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ScoreItem) {
        helper.setText(R.id.tv_sort,ToolUtils.numbers[item.sort])
        helper.setText(R.id.tv_score,item.score)

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext,3)
        val mAdapter = ChildAdapter(R.layout.item_topic_score, item.childScores)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(helper.adapterPosition,view,position)
        }
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int, view: View, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }


    class ChildAdapter(layoutResId: Int,  data: List<ScoreItem>?) : BaseQuickAdapter<ScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort,item.sort.toString())
                helper.setText(R.id.tv_score,item.score)
                helper.setImageResource(R.id.iv_result,if (item.result==1) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
                addOnClickListener(R.id.tv_score,R.id.iv_result)
            }
        }
    }

}
