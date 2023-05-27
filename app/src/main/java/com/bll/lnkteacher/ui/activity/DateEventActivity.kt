package com.bll.lnkteacher.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.DateDialog
import com.bll.lnkteacher.mvp.model.Date
import com.bll.lnkteacher.utils.DateUtils
import kotlinx.android.synthetic.main.ac_date_event.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class DateEventActivity:BaseActivity() {
    private var mDate: Date?=null
    private var dayLong = 24 * 60 * 60 * 1000
    private var nowLong=0L
    private var elik: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        mDate = intent.getBundleExtra("bundle").getSerializable("dateBean") as Date
        nowLong=mDate?.time!!
    }

    override fun initView() {
        setPageTitle("日程")
        elik = v_content.pwInterFace
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
            DateDialog(this).builder().setOnDateListener { dateStr, dateTim ->
                nowLong=dateTim
                setContentView()
            }
        }

    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date(nowLong))

        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(nowLong))+"/draw.tch"
        elik?.setPWEnabled(true)
        elik?.setLoadFilePath(path, true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(Constants.DATE_EVENT)
    }

}