package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.CalendarView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DateUtils


class DateCalendarDialog1(private val context: Context, private val date:Long){

    private var dialog:Dialog?=null

    constructor(context: Context):this(context,0)

    fun builder(): DateCalendarDialog1 {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_calendar)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val calendarView = dialog?.findViewById<CalendarView>(R.id.dp_date)
        calendarView?.firstDayOfWeek=2
        calendarView?.date = date

        calendarView?.setOnDateChangeListener { calendarView, i, i2, i3 ->
            dismiss()
            val dateToStamp = "${i}-${i2+1}-${i3}"
            val time = DateUtils.dateToStamp(dateToStamp)
            dateListener?.getDate(time)
        }
        return this
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private var dateListener: OnDateListener? = null

    fun interface OnDateListener {
        fun getDate(dateTim: Long)
    }

    fun setOnDateListener(dateListener: OnDateListener?) {
        this.dateListener = dateListener
    }


}