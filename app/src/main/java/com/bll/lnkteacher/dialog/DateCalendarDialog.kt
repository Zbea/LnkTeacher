package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.CalendarView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils


class DateCalendarDialog(private val context: Context, private val date:Long,private val x:Float,private val y:Float){

    private var dialog:Dialog?=null

    constructor(context: Context):this(context,0,0f,0f)
    constructor(context: Context,x: Float,y: Float):this(context, 0, x, y)
    constructor(context: Context,date: Long):this(context,date,0f,0f)

    fun builder(): DateCalendarDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_calendar)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (x!=0f||y!=0f){
            layoutParams.gravity = Gravity.TOP or  Gravity.END
            layoutParams.y= DP2PX.dip2px(context,y)
            layoutParams.x=DP2PX.dip2px(context,x)
        }
        dialog?.show()

        val calendarView = dialog?.findViewById<CalendarView>(R.id.dp_date)
        calendarView?.firstDayOfWeek=2
        if (date!=0L)
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