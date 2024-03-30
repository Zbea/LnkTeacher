package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkCorrectAdapter(layoutResId: Int, data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        helper.setText(R.id.tv_type,item.taskName)
        helper.setText(R.id.tv_title,item.title)
        helper.setGone(R.id.tv_analyse,item.subType!=3)
        helper.setText(R.id.tv_date_create,mContext.getString(R.string.teaching_homework_start_date)+"："+DateUtils.longToStringWeek(item.time))
        val rvList=helper.getView<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        val mAdapter = TypeAdapter(R.layout.item_testpaper_correct_class_type,item.examList)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            listener?.onClick(view,helper.adapterPosition,position)
        }
        helper.addOnClickListener(R.id.tv_analyse,R.id.iv_delete)
    }

    class TypeAdapter(layoutResId: Int,data: List<CorrectClassBean>?) : BaseQuickAdapter<CorrectClassBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: CorrectClassBean) {
            helper.setText(R.id.tv_class_name,item.name)
            helper.setText(R.id.tv_number,"${item.totalStudent}人")
            helper.setText(R.id.tv_receive_number,"${item.totalSubmitStudent}")
            helper.setText(R.id.tv_correct_number,"${item.totalSend}")
            helper.setVisible(R.id.tv_end_date,true)
            helper.setText(R.id.tv_end_date,DateUtils.longToStringWeek(item.endTime))
            helper.addOnClickListener(R.id.ll_content)
        }

    }

    private var listener: OnChildClickListener? = null

    fun interface OnChildClickListener {
        fun onClick(view: View,parentPosition:Int,position: Int)
    }

    fun setOnChildClickListener(listener: OnChildClickListener?) {
        this.listener = listener
    }

}
