package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.utils.DateUtils


class MainTeachingCopyDialog(private val context: Context, private val classId:Int) {

    private var dialog:Dialog?=null
    private var pops= mutableListOf<PopupBean>()
    private var id=0

    fun builder(): MainTeachingCopyDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_main_teaching_copy)
        dialog?.show()
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        pops= DataBeanManager.popClassGroups
        pops[0].isCheck=true
        id=pops[0].id

        val dp_start = dialog?.findViewById<DatePicker>(R.id.dp_start)
        val dp_end = dialog?.findViewById<DatePicker>(R.id.dp_end)

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        val tvCopy = dialog?.findViewById<TextView>(R.id.tv_copy)

        val tvClass = dialog?.findViewById<TextView>(R.id.tv_class)
        tvClass?.text=pops[0].name
        tvClass?.setOnClickListener {
            selectorClassGroup(tvClass)
        }

        val cbCheck = dialog?.findViewById<CheckBox>(R.id.cb_check)

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {

            val startYear = dp_start?.year
            val startMonth = dp_start?.month?.plus(1)
            val startDay = dp_start?.dayOfMonth

            val startStr = "${startYear}年${startMonth}月${startDay}日"
            val startLong = DateUtils.dateToStamp(startYear!!,startMonth!!,startDay!!)

            val endYear = dp_end?.year
            val endMonth = dp_end?.month?.plus(1)
            val endDay = dp_end?.dayOfMonth

            val endStr = "${endYear}年${endMonth}月${endDay}日"
            val endLong = DateUtils.dateToStamp(endYear!!,endMonth!!,endDay!!)

            val lists=if (cbCheck?.isChecked==true){
                DateEventDaoManager.getInstance().queryBeans(classId)
            }
            else{
                DateEventDaoManager.getInstance().queryBeans(classId,startLong,endLong)
            }

            for (item in lists){
                val dateEvent=DateEventDaoManager.getInstance().queryBean(id,item.date)
                if (dateEvent!=null)
                {
                    dateEvent.content=item.content
                    DateEventDaoManager.getInstance().insertOrReplace(dateEvent)
                }
                else{
                    item.id=null
                    item.classId=id
                    DateEventDaoManager.getInstance().insertOrReplace(item)
                }
            }

            listener?.onClick()
            dismiss()

        }
        return this
    }

    private var popWindow:PopupRadioList?=null
    private fun selectorClassGroup(view: TextView){
        if (popWindow==null)
        {
            popWindow= PopupRadioList(context, pops, view,  20).builder()
            popWindow  ?.setOnSelectListener { item ->
                view.text = item.name
                id=item.id
            }
        }
        else{
            popWindow?.show()
        }
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}