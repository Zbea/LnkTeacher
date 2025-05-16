package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DateUtils


class DateSelectorDialog(private val context: Context) {
    private var dialog:Dialog?=null

    fun builder(): DateSelectorDialog {
        dialog =Dialog(context)
        dialog?.setContentView(R.layout.dialog_date_time)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val mDatePicker = dialog?.findViewById<DatePicker>(R.id.dp_date)
        mDatePicker?.updateDate(DateUtils.getYear(),DateUtils.getMonth()-1,DateUtils.getDay())

        val tp_time = dialog?.findViewById<TimePicker>(R.id.tp_time)
        tp_time?.setIs24HourView(true)

        val cancleTv = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog?.findViewById<TextView>(R.id.tv_ok)

        cancleTv?.setOnClickListener { dismiss() }
        okTv?.setOnClickListener {
            val year = mDatePicker?.year
            val month = mDatePicker?.month?.plus(1)
            val dayOfMonth = mDatePicker?.dayOfMonth
            val date = "${year}-${month}-${dayOfMonth}"

            val hour=tp_time?.hour!!
            val minute=tp_time.minute

            val timeStr="$date ${getFormat(hour)}:${getFormat(minute)}"
            val timeLong=DateUtils.date3Stamp(timeStr)

            if (timeLong>System.currentTimeMillis()){
                dateListener?.getDate(timeStr,timeLong)
                dismiss()
            }
        }
        return this
    }

    /**
     * 格式化时间
     */
    private fun getFormat(num:Int):String{
        return if (num<10) "0$num" else "$num"
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(timeStr: String,timeLong: Long)
    }

    fun setOnDateListener(dateListener:OnDateListener) {
        this.dateListener = dateListener
    }


}