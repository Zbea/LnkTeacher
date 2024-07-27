package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.*
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectAdapter(layoutResId: Int, data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        helper.setText(tv_title,item.title)
        helper.setText(tv_exam_type,item.subTypeName)
        helper.setText(tv_self_correct,if (item.selfBatchStatus==1)"自批" else "")
        helper.setText(tv_date_create,mContext.getString(R.string.teaching_assign_time)+"："+ DateUtils.longToStringWeek(item.time))
        helper.setText(tv_date_commit,"提交时间："+ DateUtils.longToStringWeek(item.endTime))
        helper.setGone(tv_analyse, item.examList.isNotEmpty()&&item.subType!=3)
        val rvList=helper.getView<RecyclerView>(rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        ClassAdapter(R.layout.item_testpaper_correct_class_type,item.selfBatchStatus==0,item.examList).apply {
            rvList.adapter = this
            bindToRecyclerView(rvList)
            setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,position)
            }
        }
        helper.addOnClickListener(tv_analyse,iv_delete)

    }

    class ClassAdapter(layoutResId: Int,var isCorrect:Boolean,data: MutableList<TestPaperClassBean>?) : BaseQuickAdapter<TestPaperClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: TestPaperClassBean) {
            helper.apply {
                setText(tv_class_name,item.name)
                setText(tv_number,"${item.totalStudent}人")
                setText(tv_receive_number,"${item.totalSubmitStudent}")
                setVisible(tv_send,isCorrect)
                addOnClickListener(ll_content,tv_send)
            }
        }

    }

    private var listener: OnChildClickListener? = null

    fun interface OnChildClickListener {
        fun onClick(view: View,parentPos:Int,position:Int)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}
