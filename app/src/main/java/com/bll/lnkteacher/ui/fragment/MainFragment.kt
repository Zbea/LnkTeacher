package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.AUTO_REFRESH_EVENT
import com.bll.lnkteacher.Constants.Companion.CLASSGROUP_EVENT
import com.bll.lnkteacher.Constants.Companion.COURSE_EVENT
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.Constants.Companion.PRIVACY_PASSWORD_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CourseModuleDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.presenter.HomeworkCorrectPresenter
import com.bll.lnkteacher.mvp.presenter.MessagePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.*
import com.bll.lnkteacher.ui.adapter.MainHomeworkAdapter
import com.bll.lnkteacher.ui.adapter.MainMessageAdapter
import com.bll.lnkteacher.ui.adapter.MainNoteAdapter
import com.bll.lnkteacher.ui.adapter.MainTeachingAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.utils.date.LunarSolarConverter
import com.bll.lnkteacher.utils.date.Solar
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * 首页
 */
class MainFragment : BaseFragment(),IContractView.IMessageView,IContractView.IHomeworkCorrectView {

    private var mPresenter= HomeworkCorrectPresenter(this)
    private var mMessagePresenter=MessagePresenter(this)

    private var mHomeworkAdapter: MainHomeworkAdapter? = null
    private var mTeachingAdapter: MainTeachingAdapter? = null
    private var classGroups= mutableListOf<ClassGroup>()

    private var mainNoteAdapter: MainNoteAdapter? = null
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MainMessageAdapter?=null

    private var popNotes= mutableListOf<PopupBean>()

    override fun onList(message: Message) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }
    override fun onSend() {
    }
    override fun onDeleteSuccess() {
    }

    override fun onList(list: CorrectList) {
        mHomeworkAdapter?.setNewData(list.list)
    }
    override fun onSendSuccess() {
        showToast(R.string.toast_send_success)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        setTitle(R.string.main_home_title)

        popNotes.add(PopupBean(0,"随笔",R.mipmap.icon_freenote))
        popNotes.add(PopupBean(1,"日记",R.mipmap.icon_diary))
        popNotes.add(PopupBean(2,"总览",R.mipmap.icon_plan))

        tv_date_today.setOnClickListener {
            startActivity(Intent(activity, DateActivity::class.java))
        }

        ll_message.setOnClickListener {
            startActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_course.setOnClickListener {
            val courseType = SPUtil.getInt("courseType")
            startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra("courseType", courseType)
            )
        }

        tv_course_module.setOnClickListener {
            CourseModuleDialog(requireActivity()).builder().setOnClickListener { type ->
                startActivity(
                    Intent(activity, MainCourseActivity::class.java).setFlags(0)
                        .putExtra("courseType", type)
                )
            }
        }

        iv_manager.setOnClickListener {
            PopupClick(requireActivity(),popNotes,iv_manager,5).builder().setOnSelectListener{
                when (it.id) {
                    0 -> {
                        startActivity(Intent(requireActivity(),FreeNoteActivity::class.java))
                    }
                    1->{
                        if (privacyPassword!=null&&privacyPassword?.isSet==true){
                            PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                                startActivity(Intent(requireActivity(),DiaryActivity::class.java))
                            }
                        } else{
                            startActivity(Intent(requireActivity(),DiaryActivity::class.java))
                        }
                    }
                    else -> {
                        startActivity(Intent(requireActivity(),PlanOverviewActivity::class.java))
                    }
                }
            }
        }

        initMessageView()
        initHomeworkView()
        initTeachingView()
        initCourse()
        initNote()

        findNotes()
        onClassGroupEvent()
    }

    override fun lazyLoad() {
        findHomework()
        findMessages()
        fetchCommonData()
        initDateView()
    }


    //课程表相关处理
    private fun initCourse() {
        val path=FileAddress().getPathScreen("screen") + "/course.png"
        if (File(path).exists())
            GlideUtils.setImageNoCacheUrl(activity,path,iv_course)
    }

    //日历相关内容设置
    private fun initDateView() {

        val dates= DateUtils.getDateNumber(Date().time)
        val solar= Solar()
        solar.solarYear=dates[0]
        solar.solarMonth=dates[1]
        solar.solarDay=dates[2]
        val lunar= LunarSolarConverter.SolarToLunar(solar)

        val str = if (!solar.solar24Term.isNullOrEmpty()) {
            "24节气   "+solar.solar24Term
        } else {
            if (!solar.solarFestivalName.isNullOrEmpty()) {
                "节日   "+solar.solarFestivalName
            } else {
                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                    "节日   "+lunar.lunarFestivalName
                }
                else{
                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
                }
            }
        }
        tv_date_today.text=SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date().time)+"  "+str
    }

    //消息相关处理
    private fun initMessageView() {
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mMessageAdapter=MainMessageAdapter(R.layout.item_main_message, null).apply {
            rv_main_message.adapter = this
            bindToRecyclerView(rv_main_message)
        }

    }

    //收到作业
    private fun initHomeworkView() {
        rv_main_homework.layoutManager = GridLayoutManager(activity, 2)//创建布局管理
        mHomeworkAdapter = MainHomeworkAdapter(R.layout.item_main_homework, null)
        rv_main_homework.adapter = mHomeworkAdapter
        mHomeworkAdapter?.bindToRecyclerView(rv_main_homework)
        rv_main_homework.addItemDecoration(SpaceGridItemDeco(2, 35))
    }

    //教学进度
    private fun initTeachingView() {
        rv_main_teaching.layoutManager = GridLayoutManager(activity, 2)//创建布局管理
        mTeachingAdapter = MainTeachingAdapter(R.layout.item_main_teaching, null)
        rv_main_teaching.adapter = mTeachingAdapter
        mTeachingAdapter?.bindToRecyclerView(rv_main_teaching)
        rv_main_teaching.addItemDecoration(SpaceGridItemDeco(2, 35))
        mTeachingAdapter?.setOnItemClickListener { _, _, position ->
            val intent = Intent(activity, TeachingPlanActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", classGroups[position])
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

    }

    //笔记
    private fun initNote(){
        mainNoteAdapter = MainNoteAdapter(R.layout.item_main_note, null)
        rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_main_note.adapter = mainNoteAdapter
        mainNoteAdapter?.bindToRecyclerView(rv_main_note)
        mainNoteAdapter?.setOnItemClickListener { adapter, view, position ->
            gotoNote(mainNoteAdapter?.data?.get(position)!!)
        }

    }

    /**
     * 查找笔记
     */
    private fun findNotes(){
        mainNoteAdapter?.setNewData(NoteDaoManager.getInstance().queryListOther(8))
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=2
        map["type"]=1
        mMessagePresenter.getList(map,false)
    }

    private fun findHomework(){
        val map=HashMap<String,Any>()
        map["size"] = 6
        map["taskType"]=1
        mPresenter.getList(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            COURSE_EVENT -> {
                initCourse()
            }
            NOTE_EVENT,NOTE_BOOK_MANAGER_EVENT -> {
                findNotes()
            }
            CLASSGROUP_EVENT -> {
                onClassGroupEvent()
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
            AUTO_REFRESH_EVENT->{
                initDateView()
                mTeachingAdapter?.refreshDate()
            }
            PRIVACY_PASSWORD_EVENT->{
                privacyPassword=getPrivacyPasswordObj()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onClassGroupEvent() {
        classGroups=DataBeanManager.classGroups
        mTeachingAdapter?.setNewData(classGroups)
    }

}