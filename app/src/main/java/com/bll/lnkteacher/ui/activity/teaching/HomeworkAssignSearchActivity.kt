package com.bll.lnkteacher.ui.activity.teaching

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CalendarSingleDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupCheckList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignSearchBean
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelectItem
import com.bll.lnkteacher.mvp.presenter.HomeworkPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.HomeworkAssignSearchAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.cb_commit
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.cb_correct
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.ll_date
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_commit_time
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_group
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import kotlinx.android.synthetic.main.common_title.tv_btn_1

class HomeworkAssignSearchActivity:BaseActivity() , IContractView.IHomeworkPaperAssignView{

    private lateinit var mPresenter: HomeworkPaperAssignPresenter
    private var nowLong=0L
    private var grade=0
    private var items= mutableListOf<HomeworkAssignSearchBean>()
    private var mAdapter:HomeworkAssignSearchAdapter?=null
    private var classIds= mutableListOf<Int>()
    private var popGroups= mutableListOf<PopupBean>()
    private var isCommit=true
    private var isCorrect=false
    private var commitTime=0L
    private var postion=-1

    override fun onSearchAssignList(list: MutableList<HomeworkAssignSearchBean>) {
        items=list
        mAdapter?.setNewData(items)
    }

    override fun onCommitSuccess() {
        showToast(R.string.teaching_assign_success)
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        initChangeScreenData()
        grade=intent.flags
        nowLong=DateUtils.getStartOfDayInMillis()
        commitTime=System.currentTimeMillis()+Constants.dayLong
    }

    override fun initChangeScreenData() {
        mPresenter= HomeworkPaperAssignPresenter(this)
    }

    override fun initView() {
        setPageTitle(getString(R.string.teaching_pop_assign_search))
        setPageOk("发送")
        showView(ll_date)

        tv_date.setOnClickListener {
            CalendarSingleDialog(this,600f,200f).builder().setOnDateListener{
                nowLong=it
                fetchData()
            }
        }

        iv_up.setOnClickListener {
            nowLong-=Constants.dayLong
            fetchData()
        }

        iv_down.setOnClickListener {
            nowLong+=Constants.dayLong
            fetchData()
        }

        tv_group.setOnClickListener {
            PopupCheckList(this, popGroups, tv_group,  5).builder().setOnSelectListener{
                classIds.clear()
                for (item in it){
                    classIds.add(item.id)
                }
            }
        }

        tv_commit_time.text=DateUtils.longToStringWeek(commitTime)
        tv_commit_time.setOnClickListener {
            CalendarSingleDialog(this,160f,200f).builder().setOnDateListener{
                if (it<DateUtils.getStartOfDayInMillis()){
                    showToast("提交时间不能小于当天")
                }
                else{
                    tv_commit_time.text= DateUtils.longToStringWeek(it)
                    commitTime=it
                }
            }
        }

        cb_commit.isChecked=isCommit
        cb_commit.setOnCheckedChangeListener { compoundButton, b ->
            isCommit=b
        }

        cb_correct.isChecked=isCorrect
        cb_correct.setOnCheckedChangeListener { compoundButton, b ->
            isCorrect=b
        }

        tv_btn_1.setOnClickListener {
            if (classIds.size==0){
                showToast("请选择班级")
                return@setOnClickListener
            }
            if (postion>0){
                val map=HashMap<String,Any>()
                map["classIds"]=classIds
                map["showStatus"]=if (isCommit) 0 else 1
                map["endTime"]=if (isCommit)commitTime/1000 else 0
                map["selfBatchStatus"]=if (isCorrect) 1 else 0
                map["taskId"]=items[postion].taskId
                mPresenter.commitHomeworkReel(map)
            }
        }

        initRecyclerView()

        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
         mAdapter=HomeworkAssignSearchAdapter(R.layout.item_homework_search, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
             setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                val item=items[position]
                when(view.id){
                    R.id.tv_answer->{
                        ImageDialog(this@HomeworkAssignSearchActivity,item.answerUrl.split(",")).builder()
                    }
                    R.id.tv_name->{
                        ImageDialog(this@HomeworkAssignSearchActivity,item.examUrl.split(",")).builder()
                    }
                    R.id.cb_check->{
                        this@HomeworkAssignSearchActivity.postion=position
                        for (ite in items){
                            ite.isCheck=false
                        }
                        item.isCheck=true
                        notifyDataSetChanged()
                        setAssignContent()
                    }
                }
            }
        }
        rv_list?.addItemDecoration(SpaceItemDeco(40))
    }

    /**
     * 选中切换内容
     */
    private fun setAssignContent(){
        val item=items[postion]
        var classGroups= mutableListOf<ClassGroup>()
        val totalClassGroups=if (item.addType==1) DataBeanManager.getClassGroupByMains(item.grade) else DataBeanManager.getClassGroups(item.grade)
        if (item.classIds.isNullOrEmpty()){
            classGroups=totalClassGroups
        }
        else{
            for (classGroup in totalClassGroups){
                if (item.classIds.contains(classGroup.classId)){
                    classGroups.add(classGroup)
                }
            }
        }

        val classSelectItem= Gson().fromJson(item.lastConfig, HomeworkClassSelectItem::class.java)
        var selectClassIds= mutableListOf<Int>()
        if (classSelectItem!=null){
            isCorrect= classSelectItem.selfBatchStatus==1
            selectClassIds= classSelectItem.classIds
        }

        popGroups.clear()
        for (classGroup in classGroups){
            popGroups.add(PopupBean(classGroup.classId,classGroup.name,selectClassIds.contains(classGroup.classId)))
        }

        if (item.answerUrl.isNullOrEmpty()){
            cb_correct.isEnabled=false
            if (isCorrect)
                isCorrect=false
        }
        else{
            cb_correct.isEnabled=true
        }
        cb_correct.isChecked=isCorrect
    }

    override fun fetchData() {
        postion=-1
        tv_date.text= DateUtils.longToStringWeek(nowLong)
        val map=HashMap<String,Any>()
        map["startTime"]=nowLong/1000
        map["endTime"]=(nowLong+Constants.dayLong)/1000
        map["grade"]=grade
        mPresenter.getListSearch(map)
    }

}