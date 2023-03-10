package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.DateEvent
import com.bll.lnkteacher.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*


class MainTeachingPlanDialog(private val context: Context, private val classId:Int, private val date: Long) {

    private var dayLong = 24 * 60 * 60 * 1000
    private var nowLong=0L
    private var etContent:EditText?=null
    private var tvDate:TextView?=null
    private var dateEvent:DateEvent?=null

    fun builder(): MainTeachingPlanDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_main_teaching_date_event)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val cancleTv = dialog.findViewById<TextView>(R.id.tv_cancel)
        val okTv = dialog.findViewById<TextView>(R.id.tv_ok)
        val ivUp = dialog.findViewById<ImageView>(R.id.iv_up)
        val ivDown = dialog.findViewById<ImageView>(R.id.iv_down)
        etContent = dialog.findViewById(R.id.et_content)
        tvDate = dialog.findViewById(R.id.tv_date)
        nowLong=date
        setChangeView()

        ivUp?.setOnClickListener {
            nowLong-=dayLong
            setChangeView()
        }

        ivDown?.setOnClickListener {
            nowLong+=dayLong
            setChangeView()
        }

        cancleTv?.setOnClickListener { dialog.dismiss() }
        okTv?.setOnClickListener {
            val contentStr=etContent?.text.toString()
            if (contentStr.isNullOrEmpty())
                return@setOnClickListener
            if (dateEvent==null){
                dateEvent=DateEvent()
                dateEvent?.content=contentStr
                dateEvent?.date=nowLong
                dateEvent?.week=DateUtils.getWeek(nowLong)
                dateEvent?.classId=classId
            }
            else{
                dateEvent?.content=contentStr
            }
            DateEventDaoManager.getInstance().insertOrReplace(dateEvent)
            listener?.onClick()
            dialog.dismiss()
        }

        return this
    }


    private fun setChangeView(){
        dateEvent=DateEventDaoManager.getInstance().queryBean(classId,nowLong)
        tvDate?.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date(nowLong))
        if (dateEvent!=null){
            etContent?.setText(dateEvent?.content)
        }
        else{
            etContent?.setText("")
        }
    }


    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onClick()
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}