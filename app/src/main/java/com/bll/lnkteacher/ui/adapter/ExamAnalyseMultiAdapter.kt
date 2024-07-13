package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamAnalyseMultiAdapter(layoutResId: Int, data: List<AnalyseItem>?) : BaseQuickAdapter<AnalyseItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AnalyseItem) {
        helper.setText(R.id.tv_sort,ToolUtils.numbers[item.sort])
        helper.setText(R.id.tv_score,item.averageScore.toString())
        val recyclerView=helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView.layoutManager = GridLayoutManager(mContext,3)
        val mAdapter = ChildAdapter(R.layout.item_exam_analyse_score, item.childAnalyses)
        recyclerView.adapter = mAdapter
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(helper.adapterPosition,view,position)
        }
    }

    class ChildAdapter(layoutResId: Int,  data: List<AnalyseItem>?) : BaseQuickAdapter<AnalyseItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: AnalyseItem) {
            helper.setText(R.id.tv_sort,item.sort.toString())
            helper.setText(R.id.tv_score,item.averageScore.toString())
            helper.setText(R.id.tv_wrong_num,item.wrongNum.toString())
            helper.addOnClickListener(R.id.tv_wrong_num)
        }
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int, view: View, childPos: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }

}
