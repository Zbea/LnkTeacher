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

class TestPaperCorrectAdapter(layoutResId: Int, val type:Int,data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        helper.setText(tv_exam_type,item.title)
        helper.setText(tv_title,item.subTypeName)
        helper.setText(tv_date_create,mContext.getString(R.string.teaching_assign_time)+"："+ DateUtils.longToStringWeek(item.time))
        helper.setGone(tv_analyse, item.examList.isNotEmpty()&&item.subType!=3)
        val rvList=helper.getView<RecyclerView>(rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        ClassAdapter(R.layout.item_testpaper_correct_class_type, type,item.examList).apply {
            rvList.adapter = this
            bindToRecyclerView(rvList)
            setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,position)
            }
        }
        helper.addOnClickListener(tv_analyse,iv_delete)

    }

    class ClassAdapter(layoutResId: Int,val type: Int,data: MutableList<TestPaperClassBean>?) : BaseQuickAdapter<TestPaperClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: TestPaperClassBean) {
            helper.apply {
                setText(tv_class_name,item.name)
                setText(tv_number,"${item.totalStudent}人")
                setText(tv_receive_number,"${item.totalSubmitStudent}")
                if(type==1){
                    helper.setVisible(tv_end_date,item.endTime>0)
                    helper.setText(tv_end_date,DateUtils.longToStringWeek(item.endTime))
                }
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
