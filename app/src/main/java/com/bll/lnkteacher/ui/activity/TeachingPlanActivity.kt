package com.bll.lnkteacher.ui.activity

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CalendarSingleDialog
import com.bll.lnkteacher.dialog.PopupDateSelector
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.Date
import com.bll.lnkteacher.mvp.model.DateEvent
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.ui.adapter.MainTeachingDateAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.date.LunarSolarConverter
import com.bll.lnkteacher.utils.date.Solar
import kotlinx.android.synthetic.main.ac_date.rv_list
import kotlinx.android.synthetic.main.ac_teaching_plan.cb_all_copy
import kotlinx.android.synthetic.main.ac_teaching_plan.cb_all_delete
import kotlinx.android.synthetic.main.ac_teaching_plan.et_content
import kotlinx.android.synthetic.main.ac_teaching_plan.et_num
import kotlinx.android.synthetic.main.ac_teaching_plan.iv_copy_save
import kotlinx.android.synthetic.main.ac_teaching_plan.iv_delete_save
import kotlinx.android.synthetic.main.ac_teaching_plan.iv_move_save
import kotlinx.android.synthetic.main.ac_teaching_plan.iv_save
import kotlinx.android.synthetic.main.ac_teaching_plan.ll_copy
import kotlinx.android.synthetic.main.ac_teaching_plan.rg_group
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_class
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_copy_end_time
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_copy_start_time
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_delete_end_time
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_delete_start_time
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_move_end_time
import kotlinx.android.synthetic.main.ac_teaching_plan.tv_move_start_time
import kotlinx.android.synthetic.main.common_title.ll_year
import kotlinx.android.synthetic.main.common_title.tv_month
import kotlinx.android.synthetic.main.common_title.tv_year
import org.greenrobot.eventbus.EventBus

/**
 * 教学计划
 */
class TeachingPlanActivity:BaseActivity() {

    private var classGroup: ClassGroup?=null
    private var classId=0
    private var mAdapter: MainTeachingDateAdapter?=null
    private var yearPop:PopupDateSelector?=null
    private var monthPop:PopupDateSelector?=null
    private var yearNow=DateUtils.getYear()
    private var monthNow=DateUtils.getMonth()
    private var dates= mutableListOf<Date>()
    private var position=0
    private var moveStartTime=0L
    private var moveEndTime=0L
    private var copyStartTime=0L
    private var copyEndTime=0L
    private var deleteStartTime=0L
    private var deleteEndTime=0L
    private var isUp=true
    private var popClasss= mutableListOf<PopupBean>()
    private var popWindow:PopupRadioList?=null
    private var selectClassId=0
    private var yearList= mutableListOf<Int>()
    private var monthList= mutableListOf<Int>()

    override fun layoutId(): Int {
        return R.layout.ac_teaching_plan
    }
    override fun initData() {
        classGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        classId=classGroup?.classId!!

        popClasss= DataBeanManager.getClassGroupPopsOtherClassId(classId)

        for (i in 4 downTo 0){
            yearList.add(yearNow-i)
        }
        for (i in 1..5){
            yearList.add(yearNow+i)
        }

        for (i in 1..12)
        {
            monthList.add(i)
        }
    }

    override fun initView() {
        setPageTitle("${classGroup?.name}  教学计划")
        showView(ll_year)

        tv_year.text=yearNow.toString()
        tv_month.text=monthNow.toString()

        tv_year.setOnClickListener {
            if (yearPop==null){
                yearPop= PopupDateSelector(this,tv_year, yearList,0).builder()
                yearPop ?.setOnSelectorListener {
                    tv_year.text=it
                    yearNow=it.toInt()
                    position=0
                    getDates()
                }
                yearPop?.show()
            }
            else{
                yearPop?.show()
            }
        }

        tv_month.setOnClickListener {
            if (monthPop==null){
                monthPop= PopupDateSelector(this,tv_month,monthList,1).builder()
                monthPop?.setOnSelectorListener {
                    tv_month.text=it
                    monthNow=it.toInt()
                    position=0
                    getDates()
                }
                monthPop?.show()
            }
            else{
                monthPop?.show()
            }
        }

        iv_save.setOnClickListener {
            val contentStr=et_content.text.toString()
            if (contentStr.isNotEmpty()){
                hideKeyboard()
                val item=dates[position]
                if (item.dateEvent==null){
                    val dateEvent= DateEvent()
                    dateEvent.content=contentStr
                    dateEvent.date=item.time
                    dateEvent.week=DateUtils.getWeek(item.time)
                    dateEvent.classId=classGroup?.classId!!
                    item.dateEvent=dateEvent
                }else{
                    item.dateEvent.content=contentStr
                }
                DateEventDaoManager.getInstance().insertOrReplace(item.dateEvent)
                mAdapter?.notifyItemChanged(position)
                if (item.time==DateUtils.getStartOfDayInMillis())
                    EventBus.getDefault().post(Constants.CLASSGROUP_TEACHING_PLAN_EVENT)
            }
        }

        setMoveView()
        setCopyView()
        setDeleteView()
        initRecyclerView()

        Thread{
            getDates()
        }.start()
    }

    private fun initRecyclerView() {
        mAdapter = MainTeachingDateAdapter(R.layout.item_main_teaching_plan, null)
        rv_list.layoutManager = GridLayoutManager(this,7)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item=dates[position]
            if (item.time>0){
                this.position= position
                //当前点击存在内容
                if(item.dateEvent!=null){
                    et_content.setText(item.dateEvent.content)
                    et_content.setSelection(item.dateEvent.content.length)
                }
                else{
                    et_content.setText("")
                }
            }
        }
    }

    /**
     * 移动
     */
    private fun setMoveView(){
        iv_move_save.setOnClickListener {
            val dayStr=et_num.text.toString()
            if (dayStr.isEmpty())
            {
                showToast("请输入移动天数")
                return@setOnClickListener
            }
            if (checkSetTimeCorrect(moveStartTime,moveEndTime)){
                val lists=DateEventDaoManager.getInstance().queryBeans(classId,moveStartTime,moveEndTime)
                val day=dayStr.toInt()
                for (item in lists) {
                    val nowLong=if (isUp) item.date+Constants.dayLong * day else item.date- Constants.dayLong * day
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
                moveStartTime=0L
                moveEndTime=0L
                setInitTextTime(tv_move_start_time,tv_move_end_time)
                EventBus.getDefault().post(Constants.CLASSGROUP_TEACHING_PLAN_EVENT)
                getDates()
            }
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            isUp= i==R.id.rb_up
        }

        tv_move_start_time.setOnClickListener {
            CalendarSingleDialog(this,490f,520f).builder().setOnDateListener{
                moveStartTime=it
                tv_move_start_time.text=DateUtils.longToStringDataNoYear(moveStartTime)
            }
        }

        tv_move_end_time.setOnClickListener {
            CalendarSingleDialog(this,360f,520f).builder().setOnDateListener{
                moveEndTime=it
                tv_move_end_time.text=DateUtils.longToStringDataNoYear(moveEndTime)
            }
        }
    }

    private fun setCopyView(){
        if (popClasss.size>0){
            popClasss[0].isCheck=true
            selectClassId=popClasss[0].id
            tv_class.text=popClasss[0].name
        }
        else{
            disMissView(ll_copy)
        }

        tv_copy_start_time.setOnClickListener {
            CalendarSingleDialog(this,490f,785f).builder().setOnDateListener{
                copyStartTime=it
                tv_copy_start_time.text=DateUtils.longToStringDataNoYear(copyStartTime)
            }
        }

        tv_copy_end_time.setOnClickListener {
            CalendarSingleDialog(this,360f,785f).builder().setOnDateListener{
                copyEndTime=it
                tv_copy_end_time.text=DateUtils.longToStringDataNoYear(copyEndTime)
            }
        }

        tv_class.setOnClickListener {
            if (popWindow==null)
            {
                popWindow= PopupRadioList(this, popClasss, tv_class,  20).builder()
                popWindow  ?.setOnSelectListener { item ->
                    tv_class.text = item.name
                    selectClassId=item.id
                }
            }
            else{
                popWindow?.show()
            }
        }

        iv_copy_save.setOnClickListener {
            var lists= mutableListOf<DateEvent>()
            if (selectClassId==0){
                showToast("请选择班级")
                return@setOnClickListener
            }
            if (cb_all_copy?.isChecked==true){
                lists=DateEventDaoManager.getInstance().queryBeans(classId)

            }
            else{
                if (checkSetTimeCorrect(copyStartTime,copyEndTime)){
                    lists=DateEventDaoManager.getInstance().queryBeans(classId,copyStartTime,copyEndTime)
                }
            }
            if (lists.size>0){
                for (item in lists){
                    val dateEvent=DateEventDaoManager.getInstance().queryBean(selectClassId,item.date)
                    if (dateEvent!=null)
                    {
                        dateEvent.content=item.content
                        DateEventDaoManager.getInstance().insertOrReplace(dateEvent)
                    }
                    else{
                        item.id=null
                        item.classId=selectClassId
                        DateEventDaoManager.getInstance().insertOrReplace(item)
                    }
                }
                copyStartTime=0L
                copyEndTime=0L
                setInitTextTime(tv_copy_start_time,tv_copy_end_time)
                EventBus.getDefault().post(Constants.CLASSGROUP_TEACHING_PLAN_EVENT)
                showToast("复制成功")
            }
        }
    }

    private fun setDeleteView(){
        tv_delete_start_time.setOnClickListener {
            CalendarSingleDialog(this,490f,965f).builder().setOnDateListener{
                deleteStartTime=it
                tv_delete_start_time.text=DateUtils.longToStringDataNoYear(deleteStartTime)
            }
        }

        tv_delete_end_time.setOnClickListener {
            CalendarSingleDialog(this,360f,965f).builder().setOnDateListener{
                deleteEndTime=it
                tv_delete_end_time.text=DateUtils.longToStringDataNoYear(deleteEndTime)
            }
        }

        iv_delete_save.setOnClickListener {
            var lists= mutableListOf<DateEvent>()
            if (cb_all_delete?.isChecked==true){
                lists=DateEventDaoManager.getInstance().queryBeans(classId)
            }
            else{
                if (checkSetTimeCorrect(deleteStartTime,deleteEndTime)){
                   lists=DateEventDaoManager.getInstance().queryBeans(classId,deleteStartTime,deleteEndTime)
                }
            }
            if (lists.size>0){
                DateEventDaoManager.getInstance().deletes(lists)
                deleteStartTime=0L
                deleteEndTime=0L
                setInitTextTime(tv_delete_start_time,tv_delete_end_time)
                getDates()
                EventBus.getDefault().post(Constants.CLASSGROUP_TEACHING_PLAN_EVENT)
            }
        }

    }

    //根据月份获取当月日期
    private fun getDates(){
        dates.clear()
        var week=DateUtils.getMonthOneDayWeek(yearNow,monthNow-1)
        if (week==1)
            week=8

        //补齐上月差数
        for (i in 0 until week-2){
            dates.add(Date())
        }

        val max=DateUtils.getMonthMaxDay(yearNow,monthNow-1)
        for (i in 1 .. max)
        {
            dates.add(getDateBean(yearNow,monthNow,i))
        }

        if (dates.size>35){
            //补齐下月天数
            for (i in 0 until 42-dates.size){
                dates.add(Date())
            }
        }
        else{
            for (i in 0 until 35-dates.size){
                dates.add(Date())
            }
        }

        if (position==0){
            for (i in dates.indices){
                if (dates[i].isNow)
                    position=i
            }
        }

        runOnUiThread {
            setCurrentDateContent()
            mAdapter?.setNewData(dates)
        }
    }

    private fun getDateBean(year: Int, month: Int, day: Int): Date {
        val solar= Solar()
        solar.solarYear=year
        solar.solarMonth=month
        solar.solarDay=day

        val date= Date()
        date.year=year
        date.month=month
        date.day=day
        date.time= DateUtils.dateToStamp("$year-$month-$day")
        date.isNow=day== DateUtils.getDay()&&DateUtils.getMonth()==month
        date.solar= solar
        date.week= DateUtils.getWeek(date.time)
        date.lunar= LunarSolarConverter.SolarToLunar(solar)
        date.dateEvent= DateEventDaoManager.getInstance().queryBean(classGroup?.classId!!,date.time)

        return date
    }

    /**
     * 检查时间设置是否正常
     */
    private fun checkSetTimeCorrect(startTime:Long,endTime:Long):Boolean{
        if (startTime==0L||endTime==0L||startTime>endTime){
            showToast("请设置正确时间")
            return false
        }
        return true
    }

    /**
     * 设置时间初始化
     */
    private fun setInitTextTime(startTime: TextView,endTime: TextView){
        startTime.text="开始时间"
        endTime.text="结束时间"
    }

    /**
     * 设置当天内容
     */
    private fun setCurrentDateContent(){
        val dateBean=dates[position]
        val dateEvent=DateEventDaoManager.getInstance().queryBean(classId,dateBean.time)
        if (dateEvent!=null){
            et_content.setText(dateEvent.content)
            et_content.setSelection(dateEvent.content.length)
        }
        else{
            et_content.setText("")
        }
    }

}