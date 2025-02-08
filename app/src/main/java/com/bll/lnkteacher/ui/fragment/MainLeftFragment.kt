package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.manager.CalenderDaoManager
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.MainLeftPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.CalenderMyActivity
import com.bll.lnkteacher.ui.activity.DateActivity
import com.bll.lnkteacher.ui.activity.ScreenshotListActivity
import com.bll.lnkteacher.ui.activity.TeachingPlanActivity
import com.bll.lnkteacher.ui.activity.drawing.DateEventActivity
import com.bll.lnkteacher.ui.activity.drawing.PlanOverviewActivity
import com.bll.lnkteacher.ui.adapter.MainTeachingAdapter
import com.bll.lnkteacher.utils.CalenderUtils
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_main_left.iv_bg
import kotlinx.android.synthetic.main.fragment_main_left.iv_calender
import kotlinx.android.synthetic.main.fragment_main_left.iv_date
import kotlinx.android.synthetic.main.fragment_main_left.rv_main_plan
import kotlinx.android.synthetic.main.fragment_main_left.tv_calender
import kotlinx.android.synthetic.main.fragment_main_left.tv_date_today
import kotlinx.android.synthetic.main.fragment_main_left.tv_planover
import kotlinx.android.synthetic.main.fragment_main_left.tv_screenshot
import kotlinx.android.synthetic.main.fragment_main_left.v_calender_down
import kotlinx.android.synthetic.main.fragment_main_left.v_calender_up
import kotlinx.android.synthetic.main.fragment_main_left.v_date_down
import kotlinx.android.synthetic.main.fragment_main_left.v_date_up
import java.io.File
import java.util.Random

class MainLeftFragment: BaseMainFragment(),IContractView.IMainLeftView{

    private var mMainLeftPresenter=MainLeftPresenter(this,1)
    private var nowDayPos=1
    private var nowDate=0L
    private var calenderPath=""
    private var mTeachingAdapter: MainTeachingAdapter? = null
    private var classGroups= mutableListOf<ClassGroup>()


    override fun onClassList(groups: MutableList<ClassGroup>) {
        DataBeanManager.classGroups=groups
        classGroups.clear()
        grade=DataBeanManager.getClassGroupsGrade()
        for (item in DataBeanManager.classGroups){
            if (item.state==1){
                classGroups.add(item)
            }
        }
        mTeachingAdapter?.setNewData(classGroups)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_left
    }

    override fun initView() {
        setTitle(DataBeanManager.getIndexLeftData()[0].name)
        initDialog(1)

        MethodManager.setImageResource(requireActivity(),R.mipmap.icon_date_event_bg,iv_bg)

        tv_date_today.setOnClickListener {
            customStartActivity(Intent(activity, DateActivity::class.java))
        }

        tv_planover.setOnClickListener {
            customStartActivity(Intent(activity, PlanOverviewActivity::class.java))
        }

        tv_screenshot.setOnClickListener {
            customStartActivity(Intent(activity, ScreenshotListActivity::class.java))
        }

        iv_date.setOnClickListener {
            val intent = Intent(requireActivity(), DateEventActivity::class.java)
            intent.putExtra("date",nowDate)
            customStartActivity(intent)
        }

        v_date_up.setOnClickListener{
            nowDate-= Constants.dayLong
            setDateView()
        }

        v_date_down.setOnClickListener {
            nowDate+=Constants.dayLong
            setDateView()
        }

        tv_calender.setOnClickListener {
            customStartActivity(Intent(activity, CalenderMyActivity::class.java))
        }

        v_calender_up.setOnClickListener{
            if (nowDayPos>1){
                nowDayPos-=1
                setCalenderImage()
            }
        }

        v_calender_down.setOnClickListener {
            if (nowDayPos<=366){
                nowDayPos+=1
                setCalenderImage()
            }
        }

        initTeachingView()
    }

    override fun lazyLoad() {
        nowDate=DateUtils.getStartOfDayInMillis()
        setDateView()
        setCalenderView()

        onCheckUpdate()
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            mMainLeftPresenter.getClassGroups()
        }
    }

    private fun initTeachingView() {
        rv_main_plan.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mTeachingAdapter = MainTeachingAdapter(R.layout.item_main_teaching, null)
        rv_main_plan.adapter = mTeachingAdapter
        mTeachingAdapter?.bindToRecyclerView(rv_main_plan)
        rv_main_plan.addItemDecoration(SpaceItemDeco(25))
        mTeachingAdapter?.setOnItemClickListener { _, _, position ->
            val intent = Intent(activity, TeachingPlanActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", classGroups[position])
            intent.putExtra("bundle", bundle)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
            customStartActivity(intent)
        }
    }

    /**
     * 设置当天时间日历
     */
    private fun setDateView(){
        tv_date_today.text=DateUtils.longToStringWeek(nowDate)
        setDateDrawingView()
    }

    private fun setDateDrawingView(){
        val path=FileAddress().getPathImage("date",DateUtils.longToStringCalender(nowDate))+"/draw.png"
        if (File(path).exists()){
            val myBitmap= BitmapFactory.decodeFile(path)
            iv_date.setImageBitmap(myBitmap)
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
            showView(v_calender_up,v_calender_down)
            calenderPath=item.path
            setCalenderImage()
        }
        else{
            disMissView(v_calender_up,v_calender_down)
            iv_calender.setImageResource(0)
        }
    }

    private fun setCalenderImage(){
        val listFiles= FileUtils.getFiles(calenderPath)
        if (listFiles.size>0){
            val file=if (listFiles.size>nowDayPos-1){
                listFiles[nowDayPos-1]
            }
            else{
                listFiles[Random().nextInt(listFiles.size)]
            }
            GlideUtils.setImageRoundUrl(requireActivity(),file.path,iv_calender,15)
        }
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
                mTeachingAdapter?.notifyDataSetChanged()
            }
            Constants.DATE_EVENT ->{
                setDateDrawingView()
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