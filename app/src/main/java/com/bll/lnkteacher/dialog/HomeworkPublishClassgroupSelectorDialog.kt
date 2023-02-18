package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkAssign
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkPublishClassgroupSelectorDialog(val mContext: Context) {


    fun builder(): HomeworkPublishClassgroupSelectorDialog? {

        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_homework_publish_classgroup_selector)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val classs= DataBeanManager.classGroups
        val datas= mutableListOf<HomeworkAssign.ClassGroupBean>()
        for (item in classs){
            datas.add(HomeworkAssign().ClassGroupBean().apply {
                className=item.name
                classId=item.classNum.toString()
            })
        }

        var rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        var mAdapter= MyAdapter(R.layout.item_publish_classgroup_selector, datas)
        rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_date){
                DateDialog(mContext).builder().setOnDateListener { dateStr, dateTim ->
                    datas[position].date=dateTim
                    mAdapter?.notifyDataSetChanged()
                }
            }
        }

        dialog.setOnDismissListener {
            listener?.onClick(mAdapter?.data)
        }

        return this
    }

    class MyAdapter(layoutResId: Int, data: List<HomeworkAssign.ClassGroupBean>?) : BaseQuickAdapter<HomeworkAssign.ClassGroupBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkAssign.ClassGroupBean) {
            val tvDate=helper.getView<TextView>(R.id.tv_date)
            tvDate.text=if (item.date>System.currentTimeMillis()) DateUtils.longToStringDataNoYearNoHour(item.date) else "选择时间"
            val cb_class=helper.getView<CheckBox>(R.id.cb_class)
            cb_class.text=item.className
            cb_class.setOnCheckedChangeListener { compoundButton, b ->
                item.isCheck=b
            }
            val cb_commit=helper.getView<CheckBox>(R.id.cb_commit)
            cb_commit.setOnCheckedChangeListener { compoundButton, b ->
                item.isCommit=b
            }
            helper.addOnClickListener(R.id.tv_date)
        }

    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(items: List<HomeworkAssign.ClassGroupBean>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}