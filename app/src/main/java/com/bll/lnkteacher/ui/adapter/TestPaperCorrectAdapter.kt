package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.*
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectAdapter(layoutResId: Int, data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        val type=when(item.type){
            1-> "班级单考"+"  "+item.examList[0].examName
            else->"校群联考"+"  "+item.examList[0].examName
        }
        helper.setText(tv_exam_type,type)
        helper.setText(tv_date_create,mContext.getString(R.string.teaching_assign_time)+"："+ DateUtils.longToStringWeek(item.time*1000))
        helper.setText(tv_group_name,item.name)
        val rvList=helper.getView<RecyclerView>(rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        ClassAdapter(R.layout.item_testpaper_correct_class_type, item.examList).apply {
            rvList.adapter = this
            bindToRecyclerView(rvList)
            setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,position)
            }
        }
        helper.addOnClickListener(tv_data,tv_analyse,iv_delete)

    }

    class ClassAdapter(layoutResId: Int,data: MutableList<CorrectClassBean>?) : BaseQuickAdapter<CorrectClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: CorrectClassBean) {
            helper.apply {
                setText(tv_class_name,item.name)
                setText(tv_number,"${item.totalStudent}人")
                setText(tv_receive_number,"${item.totalSubmitStudent}")
                setVisible(tv_save,item.status==2&&item.totalSubmitStudent>0)
                addOnClickListener(ll_content, tv_save)
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
