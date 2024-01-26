package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamScoreDetailsDialog(val context: Context, private val totalScore:Int, val lists: MutableList<ExamScoreItem>) {

    fun builder(): ExamScoreDetailsDialog? {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_exam_score)
        val window=dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,2)
        val mAdapter = MyAdapter(R.layout.item_exam_score, lists)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDeco2(DP2PX.dip2px(context,15f), 30))
        mAdapter.bindToRecyclerView(rv_list)

        val et_total=dialog.findViewById<TextView>(R.id.et_total)
        et_total.setText(totalScore.toString())
        et_total.isFocusable = false
        et_total.isFocusableInTouchMode = false

        val tv_save=dialog.findViewById<TextView>(R.id.tv_save)
        tv_save.setOnClickListener {
            dialog.dismiss()
        }

        return this
    }


    class MyAdapter(layoutResId: Int, data: List<ExamScoreItem>?) : BaseQuickAdapter<ExamScoreItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ExamScoreItem) {
            helper.setText(R.id.tv_sort,item.sort.toString())
            helper.setText(R.id.et_score,item.score)
            helper.setGone(R.id.ll_page,false)
        }
    }

}