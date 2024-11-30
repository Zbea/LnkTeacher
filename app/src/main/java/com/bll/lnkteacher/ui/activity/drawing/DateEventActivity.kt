package com.bll.lnkteacher.ui.activity.drawing

import android.os.Handler
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CalendarSingleDialog
import com.bll.lnkteacher.utils.DateUtils
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Locale

class DateEventActivity:BaseDrawingActivity() {
    private var nowLong=0L

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        nowLong=intent.getLongExtra("date",0)
    }

    override fun initView() {
        setPageTitle("日程")
        setContentView()

        MethodManager.setImageResource(this,R.mipmap.icon_date_event_bg,v_content_b)

        iv_up.setOnClickListener {
            nowLong-=Constants.dayLong
            setContentView()
        }

        iv_down.setOnClickListener {
            nowLong+=Constants.dayLong
            setContentView()
        }

        tv_date.setOnClickListener {
            CalendarSingleDialog(this,45f,190f).builder().setOnDateListener{
                nowLong=it
                setContentView()
            }
        }
    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(nowLong)
        val path=FileAddress().getPathImage("date",DateUtils.longToStringCalender(nowLong))+"/draw.png"
        elik_b?.setLoadFilePath(path, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopErasure()
        Handler().postDelayed({
            EventBus.getDefault().post(Constants.DATE_EVENT)
        },500)
    }

}