package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.manager.CalenderDaoManager
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.ui.activity.CalenderListActivity
import com.bll.lnkteacher.ui.activity.CalenderMyActivity
import com.bll.lnkteacher.ui.activity.DateActivity
import com.bll.lnkteacher.ui.activity.TeachingPlanActivity
import com.bll.lnkteacher.ui.adapter.MainTeachingAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.utils.date.LunarSolarConverter
import com.bll.lnkteacher.utils.date.Solar
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_main_left.*
import java.io.File

class MainLeftFragment:BaseFragment() {

    private var nowDayPos=1
    private var nowDate=0L
    private var calenderPath=""
    private var mTeachingAdapter: MainTeachingAdapter? = null
    private var classGroups= mutableListOf<ClassGroup>()
    private var popupCalenders= mutableListOf<PopupBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {
        setTitle(R.string.main_home_title)

        popupCalenders.add(PopupBean(0,"台历列表"))
        popupCalenders.add(PopupBean(1,"我的台历"))

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        v_date_up.setOnClickListener{
            nowDate-= Constants.dayLong
            setDateView()
        }

        v_date_down.setOnClickListener {
            nowDate+=Constants.dayLong
            setDateView()
        }

        iv_calender_more.setOnClickListener {
            PopupClick(requireActivity(),popupCalenders,iv_calender_more,-20).builder().setOnSelectListener{
                when (it.id) {
                    0 -> {
                        customStartActivity(Intent(activity, CalenderListActivity::class.java))
                    }
                    1->{
                        customStartActivity(Intent(activity, CalenderMyActivity::class.java))
                    }
                }
            }
        }

        v_calender_up.setOnClickListener{
            if (nowDayPos>1){
                nowDayPos-=1
                setCalenderBg()
            }
        }

        v_calender_down.setOnClickListener {
            val allDay=if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
            if (nowDayPos<=allDay){
                nowDayPos+=1
                setCalenderBg()
            }
        }

        initDialog(1)

        nowDate=DateUtils.getStartOfDayInMillis()
        setDateView()
        setCalenderView()
        initTeachingView()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            fetchCommonData()
            mCommonPresenter.getAppUpdate()
        }
    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView(){
        val solar= Solar()
        solar.solarYear= DateUtils.getYear()
        solar.solarMonth=DateUtils.getMonth()
        solar.solarDay=DateUtils.getDay()
        val lunar= LunarSolarConverter.SolarToLunar(solar)

        val str = if (!solar.solar24Term.isNullOrEmpty()) {
            "24节气   "+solar.solar24Term
        } else {
            if (!solar.solarFestivalName.isNullOrEmpty()) {
                "节日  "+solar.solarFestivalName
            } else {
                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                    "节日   "+lunar.lunarFestivalName
                }
                else{
                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
                }
            }
        }
        tv_date_today.text=DateUtils.longToStringWeek(nowDate)
        setDateDrawingView()
    }

    private fun setDateDrawingView(){
        val path=FileAddress().getPathImage("date",DateUtils.longToStringCalender(nowDate))+"/draw.png"
        if (File(path).exists()){
            GlideUtils.setImageNoCacheUrl(activity,path,iv_date)
        }
        else{
            iv_date.setImageResource(0)
        }
    }

    private fun setCalenderView(){
        val calenderUtils= CalenderUtils(DateUtils.longToStringDataNoHour(nowDate))
        nowDayPos=calenderUtils.elapsedTime()
        val item= CalenderDaoManager.getInstance().queryCalenderBean()
        if (item!=null){
            calenderPath=item.path
            setCalenderBg()
        }
        else{
            iv_calender.setImageResource(0)
        }
    }

    private fun setCalenderBg(){
        val listFiles= FileUtils.getFiles(calenderPath) ?: return
        val file=if (listFiles.size>nowDayPos-1){
            listFiles[nowDayPos-1]
        }
        else{
            listFiles[listFiles.size-1]
        }
        GlideUtils.setImageFile(requireActivity(),file,iv_calender)
    }

    private fun initTeachingView() {
        rv_main_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mTeachingAdapter = MainTeachingAdapter(R.layout.item_main_teaching, null)
        rv_main_plan.adapter = mTeachingAdapter
        mTeachingAdapter?.bindToRecyclerView(rv_main_plan)
        rv_main_plan.addItemDecoration(SpaceItemDeco(0,0,0,25))
        mTeachingAdapter?.setOnItemClickListener { _, _, position ->
            val intent = Intent(activity, TeachingPlanActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", classGroups[position])
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
    }

    override fun onClassGroupEvent() {
        classGroups.clear()
        for (item in DataBeanManager.classGroups){
            if (item.state==1){
                classGroups.add(item)
            }
        }
        mTeachingAdapter?.setNewData(classGroups)
    }


    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.AUTO_REFRESH_EVENT ->{
                nowDate=DateUtils.getStartOfDayInMillis()
                setDateView()
                setCalenderView()
            }
            Constants.DATE_EVENT ->{
                setDateDrawingView()
            }
            Constants.CLASSGROUP_EVENT->{
                fetchCommonData()
            }
            Constants.CLASSGROUP_TEACHING_PLAN_EVENT->{
                mTeachingAdapter?.notifyDataSetChanged()
            }
            Constants.CALENDER_SET_EVENT->{
                setCalenderView()
            }
        }
    }


}