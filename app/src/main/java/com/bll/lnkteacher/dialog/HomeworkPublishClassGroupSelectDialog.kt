package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkPublishClassGroupSelectDialog(val mContext: Context,val grade:Int,val typeId:Int) {

    private var dialog:Dialog?=null
    private var initDatas= mutableListOf<HomeworkClass>()

    fun builder(): HomeworkPublishClassGroupSelectDialog {

        dialog = Dialog(mContext)
        dialog?.setContentView(R.layout.dialog_homework_publish_classgroup_select)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val classs= DataBeanManager.getGradeClassGroups(grade)
        val homeworkClasss= MethodManager.getCommitClass(typeId)

        for (item in classs){
            initDatas.add(HomeworkClass().apply {
                className=item.name
                classId=item.id
                date=DateUtils.getStartOfDayInMillis()+Constants.dayLong
            })
        }

        for (item in initDatas){
            for (selectItem in homeworkClasss){
                if (item.classId==selectItem.classId){
                    item.isCheck=selectItem.isCheck
                    item.isCommit=selectItem.isCommit
                }
            }
        }

        val rvList=dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter= MyAdapter(R.layout.item_publish_classgroup_selector, initDatas)
        rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList.addItemDecoration(SpaceItemDeco(0,0,0, 20))
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item=initDatas[position]
            when (view.id){
                R.id.tv_date->{
                    DateCalendarDialog(mContext,item.date).builder().setOnDateListener{
                        item.date=it
                        mAdapter?.notifyDataSetChanged()
                    }
                }
                R.id.cb_class->{
                    item.isCheck=!item.isCheck
                    mAdapter?.notifyDataSetChanged()
                }
                R.id.cb_commit->{
                    item.isCommit=!item.isCommit
                    item.submitStatus=if (item.isCommit) 0 else 1
                    mAdapter?.notifyDataSetChanged()
                }
            }

        }

        dialog?.setOnDismissListener {
            listener?.onSelect(getSelectClass())
        }

        return this
    }

    /**
     * 得到选中的班级信息
     */
    private fun getSelectClass():MutableList<HomeworkClass>{
        val items= mutableListOf<HomeworkClass>()
        for (item in initDatas){
            if (item.isCheck){
                if (!item.isCommit)
                    item.date=0
                items.add(item)
            }
        }
        return items
    }

    fun show(){
        dialog?.show()
    }

    class MyAdapter(layoutResId: Int, data: List<HomeworkClass>?) : BaseQuickAdapter<HomeworkClass, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkClass) {
            helper.setText(R.id.cb_class,"  "+item.className)
            helper.setChecked(R.id.cb_class,item.isCheck)
            helper.setChecked(R.id.cb_commit,item.isCommit)
            helper.setVisible(R.id.tv_date,item.isCommit)
            helper.setText(R.id.tv_date,if (item.date>System.currentTimeMillis()) DateUtils.longToStringDataNoYearNoHour(item.date) else "选择时间")
            helper.addOnClickListener(R.id.tv_date,R.id.cb_class,R.id.cb_commit)
        }

    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSelect(items: MutableList<HomeworkClass>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}