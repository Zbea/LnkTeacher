package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.*
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrect
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectAdapter(layoutResId: Int, data: List<TestPaperCorrect.CorrectBean>?) : BaseQuickAdapter<TestPaperCorrect.CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaperCorrect.CorrectBean) {
        when(item.type){
            1-> helper.setText(tv_exam_type,"班群单考")
            2->helper.setText(tv_exam_type,"校群联考")
            else->helper.setText(tv_exam_type,"际群联考")
        }
        helper.setText(tv_date_create,"布置时间  "+ DateUtils.longToStringWeek(item.time*1000))
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
        helper.addOnClickListener(tv_data,tv_analyse,iv_delete,tv_student)

    }

    class ClassAdapter(layoutResId: Int,data: MutableList<TestPaperCorrect.ClassBean>?) : BaseQuickAdapter<TestPaperCorrect.ClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: TestPaperCorrect.ClassBean) {
            helper.apply {
                setText(tv_type,item.examName)
                setText(tv_class_name,item.name)
                setText(tv_number,"${item.totalStudent}人")
                setText(tv_receive_number,"${item.totalSubmitStudent}人")
                setVisible(tv_save,item.status==2)
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
