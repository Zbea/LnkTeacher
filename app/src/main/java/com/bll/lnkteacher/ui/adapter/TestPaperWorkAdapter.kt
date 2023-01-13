package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.TestPaperWork
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperWorkAdapter(layoutResId: Int, data: List<TestPaperWork>?) : BaseQuickAdapter<TestPaperWork, BaseViewHolder>(layoutResId, data) {

    var mAdapter:TypeAdapter?=null

    override fun convert(helper: BaseViewHolder, item: TestPaperWork) {
        helper.setText(R.id.tv_exam_type,item.type)
        helper.setText(R.id.tv_date_commit,"上交时间  "+ DateUtils.longToStringDataNoYearNoHour(item.commitDate))
        helper.setText(R.id.tv_date_create,"布置时间  "+ DateUtils.longToStringWeek(item.createDate))

        var rvList=helper.getView<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        mAdapter = TypeAdapter(R.layout.item_testpaper_work_type, item.testPaperType,item.lists)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(view,position)
        }

        helper.addOnClickListener(R.id.tv_data,R.id.tv_analyse,R.id.iv_delete,R.id.tv_student)

    }

    class TypeAdapter(layoutResId: Int,var testType:String,data: List<TestPaperWork.ListBean>?) : BaseQuickAdapter<TestPaperWork.ListBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: TestPaperWork.ListBean) {
            helper.setText(R.id.tv_type,testType)
            helper.setText(R.id.tv_class_name,item.className)
            helper.setText(R.id.tv_number,"${item.number}人")
            helper.setText(R.id.tv_receive_number,"${item.receiveNumber}人")
        }

    }

    private var listener: OnChildClickListener? = null

    interface OnChildClickListener {
        fun onClick(view: View,position:Int)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}
