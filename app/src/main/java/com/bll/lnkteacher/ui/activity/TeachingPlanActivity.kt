package com.bll.lnkteacher.ui.activity

import PopupClick
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.MainTeachingCopyDialog
import com.bll.lnkteacher.dialog.MainTeachingDeleteDialog
import com.bll.lnkteacher.dialog.MainTeachingMoveDialog
import com.bll.lnkteacher.dialog.MainTeachingPlanDialog
import com.bll.lnkteacher.manager.DateEventDaoManager
import com.bll.lnkteacher.mvp.model.Date
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.ui.adapter.MainTeachingDateAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.date.LunarSolarConverter
import com.bll.lnkteacher.utils.date.Solar
import kotlinx.android.synthetic.main.ac_date.*
import org.greenrobot.eventbus.EventBus

/**
 * 教学计划
 */
class TeachingPlanActivity:DateActivity() {

    private var classGroup: ClassGroup?=null
    private var pops= mutableListOf<PopupBean>()
    private var mAdapter: MainTeachingDateAdapter?=null

    override fun initData() {
        super.initData()
        classGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup

        pops.add(PopupBean(0, "教学移动", false))
        pops.add(PopupBean(1, "教学复制", false))
        pops.add(PopupBean(2, "教学删除", false))
    }

    override fun initView() {
        super.initView()
        setPageTitle("${classGroup?.name}  教学计划")
        showView(iv_manager)

        iv_manager.setOnClickListener {
            showPopView()
        }

    }

    override fun initRecycler() {
        mAdapter = MainTeachingDateAdapter(R.layout.item_main_teaching_plan, null)
        rv_list.layoutManager = GridLayoutManager(this,7)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val dateBean=dates[position]
            if (dateBean.time>0){
                MainTeachingPlanDialog(this,classGroup?.classId!!,dateBean.time).builder()
                    ?.setOnClickListener{
                        getDates()
                    }
            }
        }
    }

    override fun getDates() {
        super.getDates()
        mAdapter?.setNewData(dates)
    }

    override fun getDateBean(year: Int, month: Int, day: Int, isMonth: Boolean): Date {

        val solar= Solar()
        solar.solarYear=year
        solar.solarMonth=month
        solar.solarDay=day

        val date= Date()
        date.year=year
        date.month=month
        date.day=day
        date.time= DateUtils.dateToStamp("$year-$month-$day")
        date.isNow=day== DateUtils.getDay()
        date.isNowMonth=isMonth
        date.solar= solar
        date.week= DateUtils.getWeek(date.time)
        date.lunar= LunarSolarConverter.SolarToLunar(solar)
        date.dateEvent=DateEventDaoManager.getInstance().queryBean(classGroup?.classId!!,date.time)

        return date

    }

    private fun showPopView(){
        PopupClick(this, pops, iv_manager,10).builder()
            .setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        move()
                    }
                    1 -> {
                        copy()
                    }
                    2 -> {
                        delete()
                    }
                }
            }
    }

    private fun move(){
        MainTeachingMoveDialog(this,classGroup?.classId!!).builder()
            .setOnDialogClickListener{
                getDates()
        }
    }

    private fun delete(){
        MainTeachingDeleteDialog(this,classGroup?.classId!!).builder()
            .setOnDialogClickListener{
                getDates()
        }
    }

    private fun copy(){
        MainTeachingCopyDialog(this,classGroup?.classId!!).builder()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

}