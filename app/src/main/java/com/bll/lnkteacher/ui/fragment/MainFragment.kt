package com.bll.lnkteacher.ui.fragment

import android.annotation.SuppressLint
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
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.manager.FreeNoteDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.*
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
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


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

    private var uploadType=0//上传类型

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

    @SuppressLint("WrongConstant")
    override fun initView() {
        setTitle(R.string.main_home_title)

        tv_date_today.setOnClickListener {
            startActivity(Intent(activity, DateActivity::class.java))
        }

        ll_message.setOnClickListener {
            startActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_course.setOnClickListener {
            startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(1)
                .putExtra("classGroupId", 0)
            )
        }


        tv_diary.setOnClickListener {
            if (privacyPassword!=null&&privacyPassword?.isSet==true){
                PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                    startActivity(Intent(requireActivity(),DiaryActivity::class.java))
                }
            } else{
                startActivity(Intent(requireActivity(),DiaryActivity::class.java))
            }
        }

        tv_free_note.setOnClickListener {
            startActivity(Intent(requireActivity(),FreeNoteActivity::class.java))
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
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            findHomework()
            findMessages()
            fetchCommonData()
            mCommonPresenter.getAppUpdate()
        }
        initDateView()
    }


    //课程表相关处理
    private fun initCourse() {
        val path=FileAddress().getPathCourse("course") + "/course.png"
        if (File(path).exists()){
            GlideUtils.setImageNoCacheUrl(activity,path,iv_course)
        }
        else{
            iv_course.setImageResource(0)
        }
    }

    //日历相关内容设置
    private fun initDateView() {
//
//        val dates= DateUtils.getDateNumber(Date().time)
//        val solar= Solar()
//        solar.solarYear=dates[0]
//        solar.solarMonth=dates[1]
//        solar.solarDay=dates[2]
//        val lunar= LunarSolarConverter.SolarToLunar(solar)
//
//        val str = if (!solar.solar24Term.isNullOrEmpty()) {
//            "24节气   "+solar.solar24Term
//        } else {
//            if (!solar.solarFestivalName.isNullOrEmpty()) {
//                "节日   "+solar.solarFestivalName
//            } else {
//                if (!lunar.lunarFestivalName.isNullOrEmpty()) {
//                    "节日   "+lunar.lunarFestivalName
//                }
//                else{
//                    lunar.getChinaMonthString(lunar.lunarMonth)+"月"+lunar.getChinaDayString(lunar.lunarDay)
//                }
//            }
//        }
        tv_date_today.text=SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date().time)
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


    /**
     * 每年上传日记
     */
    fun uploadDiary(token:String){
        cloudList.clear()
        val nullItems= mutableListOf<DiaryBean>()
        val diarys=DiaryDaoManager.getInstance().queryList()
        for (diaryBean in diarys){
            val fileName=DateUtils.longToString(diaryBean.date)
            val path=FileAddress().getPathDiary(fileName)
            if (!FileUtils.getFiles(path).isNullOrEmpty()){
                FileUploadManager(token).apply {
                    startUpload(path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=4
                            subType=-1
                            subTypeStr="日记"
                            year=DateUtils.getYear()
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(diaryBean)
                            downloadUrl=it
                        })
                        //当加入上传的内容等于全部需要上传时候，则上传
                        if (cloudList.size== diarys.size-nullItems.size){
                            mCloudUploadPresenter.upload(cloudList)
                            uploadType=1
                        }
                    }
                }
            }
            else{
                //没有内容不上传
                nullItems.add(diaryBean)
            }
        }
    }

    /**
     * 每年上传随笔
     */
    fun uploadFreeNote(token:String){
        cloudList.clear()
        val beans=FreeNoteDaoManager.getInstance().queryList()
        val nullItems= mutableListOf<FreeNoteBean>()
        for (item in beans){
            val fileName=DateUtils.longToString(item.date)
            val path=FileAddress().getPathFreeNote(fileName)
            if (!FileUtils.getFiles(path).isNullOrEmpty()){
                FileUploadManager(token).apply {
                    startUpload(path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=5
                            subType=-1
                            subTypeStr="随笔"
                            year=DateUtils.getYear()
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(item)
                            downloadUrl=it
                        })
                        //当加入上传的内容等于全部需要上传时候，则上传
                        if (cloudList.size== beans.size-nullItems.size){
                            mCloudUploadPresenter.upload(cloudList)
                            uploadType=2
                        }
                    }
                }
            }
            else{
                //没有内容不上传
                nullItems.add(item)
            }
        }
    }

    /**
     * 每年上传截图
     */
    fun uploadScreenShot(token:String){
        cloudList.clear()
        val screenTypes= ItemTypeDaoManager.getInstance().queryAll(3)
        val nullItems= mutableListOf<ItemTypeBean>()
        val itemTypeBean=ItemTypeBean()
        itemTypeBean.title="未分类"
        itemTypeBean.date=System.currentTimeMillis()
        itemTypeBean.path=FileAddress().getPathScreen("未分类")
        screenTypes.add(itemTypeBean)
        for (item in screenTypes){
            val fileName=DateUtils.longToString(item.date)
            val path=item.path
            if (!FileUtils.getFiles(path).isNullOrEmpty()){
                FileUploadManager(token).apply {
                    startUpload(path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=6
                            subType=-1
                            subTypeStr=item.title
                            year=DateUtils.getYear()
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(item)
                            downloadUrl=it
                        })
                        //当加入上传的内容等于全部需要上传时候，则上传
                        if (cloudList.size== screenTypes.size-nullItems.size){
                            mCloudUploadPresenter.upload(cloudList)
                            uploadType=3
                        }
                    }
                }
            }
            else{
                //没有内容不上传
                nullItems.add(item)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        when(uploadType){
            1->{
                val path=FileAddress().getPathDiary(DateUtils.longToString(System.currentTimeMillis()))
                FileUtils.deleteFile(File(path).parentFile)
                DiaryDaoManager.getInstance().clear()
            }
            2->{
                val path=FileAddress().getPathFreeNote(DateUtils.longToString(System.currentTimeMillis()))
                FileUtils.deleteFile(File(path).parentFile)
                FreeNoteDaoManager.getInstance().clear()
            }
            3->{
                val path=FileAddress().getPathScreen("未分类")
                FileUtils.deleteFile(File(path).parentFile)
                ItemTypeDaoManager.getInstance().clear(3)
            }
        }
    }

}