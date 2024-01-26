package com.bll.lnkteacher.ui.activity

import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.DateCalendarDialog1
import com.bll.lnkteacher.mvp.model.Date
import com.bll.lnkteacher.utils.DateUtils
import kotlinx.android.synthetic.main.ac_date_event.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class DateEventActivity:BaseDrawingActivity() {
    private var mDate: Date?=null
    private var dayLong = 24 * 60 * 60 * 1000
    private var nowLong=0L
    private var isDraw=false

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        mDate = intent.getBundleExtra("bundle")?.getSerializable("dateBean") as Date
        nowLong=mDate?.time!!
    }

    override fun initView() {
        setPageTitle("日程")
        setContentView()

        iv_up.setOnClickListener {
            nowLong-=dayLong
            setContentView()
        }

        iv_down.setOnClickListener {
            nowLong+=dayLong
            setContentView()
        }

        tv_date.setOnClickListener {
            DateCalendarDialog1(this).builder().setOnDateListener{
                nowLong=it
                setContentView()
            }
        }

    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(nowLong)
        val path=FileAddress().getPathImage("date",DateUtils.longToStringCalender(nowLong))+"/draw.tch"
        elik?.setLoadFilePath(path, true)
    }

    override fun onElikSave() {
        elik?.saveBitmap(true) {}
        isDraw=true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isDraw)
            EventBus.getDefault().post(Constants.DATE_EVENT)
    }

}