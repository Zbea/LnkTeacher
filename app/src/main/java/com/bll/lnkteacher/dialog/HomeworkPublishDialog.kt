package com.bll.lnkteacher.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.KeyboardUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson

class HomeworkPublishDialog(val context: Context,val typeBean: TypeBean) {

    private var endTime=0L
    private var isCommit=true

    @SuppressLint("SuspiciousIndentation")
    fun builder(): HomeworkPublishDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_publish)
        dialog.setCanceledOnTouchOutside(false)
        val window=dialog.window!!
            window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,600f))/2
        dialog.show()

        val tv_send = dialog.findViewById<TextView>(R.id.tv_ok)
        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val cb_commit=dialog.findViewById<CheckBox>(R.id.cb_commit)
        val tv_date = dialog.findViewById<TextView>(R.id.tv_date)
        val etContent = dialog.findViewById<EditText>(R.id.et_content)
        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)

        val classSelectItem= Gson().fromJson(typeBean.lastConfig, HomeworkAssignItem::class.java)
        var classIds:MutableList<Int>?=null
        if (classSelectItem!=null){
            classIds= classSelectItem.classIds
        }

        endTime=System.currentTimeMillis()+Constants.dayLong
        tv_date.text=DateUtils.longToStringWeek(endTime)
        cb_commit.isChecked=isCommit

        var allClassGroups= mutableListOf<ClassGroup>()
        //判断是否绑定了班群
        if (typeBean.classIds.isNullOrEmpty()){
            allClassGroups=if (typeBean.addType==1) DataBeanManager.getClassGroupExcpetChilds(typeBean.grade) else DataBeanManager.getClassGroups(typeBean.grade)
        }
        else{
            val bindClassIds=typeBean.classIds.split(",")
            for (classGroup in DataBeanManager.getClassGroups(typeBean.grade)){
                if (bindClassIds.contains(classGroup.classId.toString())){
                    allClassGroups.add(classGroup)
                }
            }
        }

        val items= mutableListOf<ClassGroup>()
        for (item in allClassGroups){
            if (!classIds.isNullOrEmpty()&&classIds.contains(item.classId)){
                item.isCheck=true
            }
            items.add(item)
        }

        cb_commit.setOnCheckedChangeListener { compoundButton, b ->
            isCommit=b
        }

        tv_date.setOnClickListener {
            CalendarSingleDialog(context,2).builder().setOnDateListener{
                if (it>System.currentTimeMillis()){
                    endTime=it
                    tv_date.text=DateUtils.longToStringWeek(endTime)
                }
            }
        }

        rvList?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_classgroup_selector, items)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item=mAdapter.getItem(position)
            item!!.isCheck=!item.isCheck
            mAdapter.notifyItemChanged(position)
        }

        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        tv_send.setOnClickListener {
            val contentStr = etContent.text.toString()
            if (contentStr.isNotEmpty()) {
                val classIds= mutableListOf<Int>()
                for (item in items){
                    if (item.isCheck){
                        classIds.add(item.classId)
                    }
                }
                if (classIds.isNotEmpty())
                {
                    val classSelect= HomeworkAssignItem()
                    classSelect.contentStr=contentStr
                    classSelect.classIds=classIds
                    classSelect.endTime=if (isCommit) endTime else 0L
                    classSelect.showStatus=if (isCommit) 0 else 1
                    listener?.onSend(classSelect)
                    dialog.dismiss()
                }
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSend(classSelect: HomeworkAssignItem)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ClassGroup) {
            helper.setGone(R.id.iv_space,item.state!=1)
            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.cb_check,if(item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
        }
    }

}