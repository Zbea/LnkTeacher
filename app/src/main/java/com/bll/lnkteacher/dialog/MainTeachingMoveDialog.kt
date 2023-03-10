package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.utils.DateUtils


class MainTeachingMoveDialog(private val context: Context,private val classId:Int) {

    private var dialog:Dialog?=null
    private val long=24*60*60*1000

    fun builder(): MainTeachingMoveDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_main_teaching_move)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val dp_start = dialog?.findViewById<DatePicker>(R.id.dp_start)
        val dp_end = dialog?.findViewById<DatePicker>(R.id.dp_end)

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog?.findViewById<TextView>(R.id.tv_ok)
        val et_num = dialog?.findViewById<EditText>(R.id.et_num)
        val cbUp = dialog?.findViewById<CheckBox>(R.id.cb_up)
        val cbDown = dialog?.findViewById<CheckBox>(R.id.cb_down)

        cbUp?.setOnClickListener {
            cbDown?.isChecked=false
        }
        cbDown?.setOnClickListener {
            cbUp?.isChecked=false
        }

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

            val lists=DateEventDaoManager.getInstance().queryBeans(classId,startLong,endLong)
            val day=et_num?.text?.toString()?.toInt()

            if (cbUp?.isChecked==false&&cbDown?.isChecked==false)
                return@setOnClickListener
            for (item in lists) {
                val nowLong=if (cbDown?.isChecked==true) item.date-long* day!! else item.date+long* day!!
                val dateEvent=DateEventDaoManager.getInstance().queryBean(classId,nowLong)
                if (dateEvent!=null)
                {
                    dateEvent.content=item.content
                    DateEventDaoManager.getInstance().insertOrReplace(dateEvent)
                    DateEventDaoManager.getInstance().deleteBean(item)
                }
                else{
                    item.date=nowLong
                    DateEventDaoManager.getInstance().insertOrReplace(item)
                }

            }
            listener?.onClick()
            dismiss()

        }
        return this
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