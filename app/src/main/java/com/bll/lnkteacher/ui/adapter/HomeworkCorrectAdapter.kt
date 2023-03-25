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
        helper.setText(R.id.tv_title,item.title)
        helper.setText(R.id.tv_date_create,mContext.getString(R.string.teaching_homework_start_date)+"："+DateUtils.longToStringWeek(item.time*1000))
        var rvList=helper.getView<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        val mAdapter = TypeAdapter(R.layout.item_testpaper_correct_class_type,item.examList)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(view,helper.adapterPosition,position)
        }

        helper.addOnClickListener(R.id.iv_delete,R.id.tv_student)
    }

    class TypeAdapter(layoutResId: Int,data: List<CorrectClassBean>?) : BaseQuickAdapter<CorrectClassBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: CorrectClassBean) {
            helper.setText(R.id.tv_type,item.examName)
            helper.setText(R.id.tv_class_name,item.name)
            helper.setText(R.id.tv_number,"${item.totalStudent}")
            helper.setText(R.id.tv_receive_number,"${item.totalSubmitStudent}")
            helper.setVisible(R.id.tv_end_date,true)
            helper.setText(R.id.tv_end_date,DateUtils.longToStringWeek(item.endTime*1000))
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
