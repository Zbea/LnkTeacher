package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.R.id.iv_delete
import com.bll.lnkteacher.R.id.ll_content
import com.bll.lnkteacher.R.id.rv_list
import com.bll.lnkteacher.R.id.tv_analyse
import com.bll.lnkteacher.R.id.tv_class_name
import com.bll.lnkteacher.R.id.tv_correct_number
import com.bll.lnkteacher.R.id.tv_date_commit
import com.bll.lnkteacher.R.id.tv_date_create
import com.bll.lnkteacher.R.id.tv_exam_type
import com.bll.lnkteacher.R.id.tv_number
import com.bll.lnkteacher.R.id.tv_receive_number
import com.bll.lnkteacher.R.id.tv_self_correct
import com.bll.lnkteacher.R.id.tv_send
import com.bll.lnkteacher.R.id.tv_title
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectAdapter(layoutResId: Int, data: List<CorrectBean>?) : BaseQuickAdapter<CorrectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CorrectBean) {
        val isShow = when(item.subType){
            3,6->{
                false
            }
            else->{
                true
            }
        }
        helper.setText(tv_title,item.title)
        helper.setText(tv_exam_type,item.subTypeName)
        helper.setText(tv_self_correct,if (item.selfBatchStatus==1)"自批" else "")
        helper.setText(tv_date_create,mContext.getString(R.string.teaching_assign_time)+"："+ DateUtils.longToStringWeek(DateUtils.dateStrToLong(item.createTime)))
        helper.setText(tv_date_commit,"提交时间："+ if (item.taskType==1) DateUtils.longToStringWeek(item.endTime) else DateUtils.longToStringNoYear1(item.endTime))
        helper.setGone(tv_analyse, !item.examList.isNullOrEmpty()&&isShow)
        helper.setGone(tv_send,isShow)

        val rvList=helper.getView<RecyclerView>(rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)//创建布局管理
        ClassAdapter(R.layout.item_testpaper_correct_class_type,item.examList).apply {
            rvList.adapter = this
            bindToRecyclerView(rvList)
            setOnItemChildClickListener { adapter, view, position ->
                listener?.onClick(view,helper.adapterPosition,position)
            }
        }
        helper.addOnClickListener(tv_analyse,iv_delete, tv_send)

    }

    class ClassAdapter(layoutResId: Int,data: MutableList<TestPaperClassBean>?) : BaseQuickAdapter<TestPaperClassBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: TestPaperClassBean) {
            helper.apply {
                setText(tv_class_name,item.name)
                setText(tv_number,"${item.totalStudent}人")
                setText(tv_receive_number,"${item.totalSubmitStudent}")
                setText(tv_correct_number,"${item.totalUpdate}")
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
