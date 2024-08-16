package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.*
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamListAdapter(layoutResId: Int, data: List<ExamList.ExamBean>?) : BaseQuickAdapter<ExamList.ExamBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ExamList.ExamBean) {
        helper.setText(tv_exam_type,item.examName)
        helper.setText(tv_date_create,"考试时间："+ DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
        helper.setText(tv_date_commit,"提交时间："+ DateUtils.longToStringNoYear1(item.endTime))
        helper.setGone(tv_send,item.sendStatus==2)
        helper.setGone(iv_delete,item.sendStatus==2)
        helper.setGone(tv_analyse,item.sendStatus==2)
        val rvList=helper.getView<RecyclerView>(rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        ClassAdapter(R.layout.item_testpaper_correct_class_type, item.classList).apply {
            rvList.adapter = this
            bindToRecyclerView(rvList)
            setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,position)
            }
        }
        helper.addOnClickListener(tv_send,tv_analyse, iv_delete)

    }

    class ClassAdapter(layoutResId: Int,data: MutableList<ExamList.ExamClassBean>?) : BaseQuickAdapter<ExamList.ExamClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ExamList.ExamClassBean) {
            helper.apply {
                setText(tv_class_name,item.className)
//                setText(tv_number,"${item.studentCount}人")
                setText(tv_receive_number,"${item.sendCount}")
                setText(tv_correct_info,"全班")
                setText(tv_correct_number,"${item.studentCount}")
                addOnClickListener(ll_content)
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
