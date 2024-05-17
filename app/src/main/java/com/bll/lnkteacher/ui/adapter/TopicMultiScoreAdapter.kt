package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TopicMultiScoreAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
        helper.setText(R.id.tv_sort,ToolUtils.numbers[item.sort])
        helper.setText(R.id.tv_score,item.score)
        helper.setVisible(R.id.iv_tips,item.isCheck)

        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = GridLayoutManager(mContext,6)
        val mAdapter = ChildAdapter(R.layout.item_topic_child_score, item.childScores)
        recyclerView?.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_score){
                listener?.onClick(helper.adapterPosition,view,position)
            }
        }

        helper.addOnClickListener(R.id.tv_score,R.id.tv_sort)
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int, view: View, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }


    class ChildAdapter(layoutResId: Int,  data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
            helper.apply {
                helper.setText(R.id.tv_sort,item.sort.toString())
                helper.setText(R.id.tv_score,item.score)
                addOnClickListener(R.id.tv_score)
            }
        }
    }

}
