package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkAssign
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignDetailsDialog(val mContext: Context, private val items:List<HomeworkAssign>) {


    fun builder(): HomeworkAssignDetailsDialog {

        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_homework_assign_details)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter= ListAdapter(R.layout.item_homework_assign_details, items)
        rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)

        return this
    }

    class ListAdapter(layoutResId: Int, data: List<HomeworkAssign>) : BaseQuickAdapter<HomeworkAssign, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkAssign) {
            helper.setText(R.id.tv_homework_type,item.homeworkType)
            helper.setText(R.id.tv_message,item.content)
            helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item.date))

            var rvList=helper.getView<RecyclerView>(R.id.rv_list)
            var mAdapter= MyAdapter(R.layout.item_homework_assign_details_classgroup, item.lists)
            rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
            rvList.adapter = mAdapter
            mAdapter?.bindToRecyclerView(rvList)

        }


        class MyAdapter(layoutResId: Int, data: List<HomeworkAssign.ClassGroupBean>?) : BaseQuickAdapter<HomeworkAssign.ClassGroupBean, BaseViewHolder>(layoutResId, data) {

            override fun convert(helper: BaseViewHolder, item: HomeworkAssign.ClassGroupBean) {
                helper.setText(R.id.tv_class_name,item.className)
                helper.setText(R.id.tv_date,DateUtils.longToStringDataNoYearNoHour(item.date))
            }

        }

    }


}