package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.COURSE_EVENT
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.DiaryManageDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.manager.FreeNoteDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.FreeNoteBean
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.mvp.presenter.MainPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.ClassScheduleActivity
import com.bll.lnkteacher.ui.activity.MessageListActivity
import com.bll.lnkteacher.ui.activity.drawing.DiaryActivity
import com.bll.lnkteacher.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkteacher.ui.adapter.MainMessageAdapter
import com.bll.lnkteacher.ui.adapter.MainNoteAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_right.iv_course
import kotlinx.android.synthetic.main.fragment_main_right.ll_course
import kotlinx.android.synthetic.main.fragment_main_right.ll_message
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_message
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_note
import kotlinx.android.synthetic.main.fragment_main_right.tv_diary
import kotlinx.android.synthetic.main.fragment_main_right.tv_free_note
import org.greenrobot.eventbus.EventBus
import java.io.File

class MainRightFragment : BaseMainFragment(),IContractView.IMainView {

    private var mPresenter=MainPresenter(this,2)
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MainMessageAdapter?=null

    private var notes= mutableListOf<Note>()
    private var mNoteAdapter:MainNoteAdapter?=null

    private var uploadType=0//上传类型
    private var privacyPassword:PrivacyPassword?=null
    private var diaryStartLong=0L
    private var diaryEndLong=0L
    private var diaryUploadTitleStr=""

    override fun onList(message: Message) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }

    override fun onClassSchedule(url: String) {
        GlideUtils.setImageNoCacheUrl(requireActivity(),url,iv_course)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        privacyPassword=MethodManager.getPrivacyPassword(0)

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_course.setOnClickListener {
            customStartActivity(Intent(activity, ClassScheduleActivity::class.java).setFlags(1)
                .putExtra("classGroupId", 0)
            )
        }

        tv_diary.setOnClickListener {
            if (privacyPassword!=null&&privacyPassword?.isSet==true){
                PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                    customStartActivity(Intent(requireActivity(), DiaryActivity::class.java))
                }
            } else{
                customStartActivity(Intent(requireActivity(), DiaryActivity::class.java))
            }
        }

        tv_diary.setOnLongClickListener {
            val pops= mutableListOf<PopupBean>()
            if (privacyPassword==null){
                pops.add(PopupBean(1,"设置密码"))
            }
            else{
                if (privacyPassword?.isSet==true){
                    pops.add(PopupBean(1,"取消密码"))
                }
                else{
                    pops.add(PopupBean(1,"设置密码"))
                }
            }
            pops.add(PopupBean(2,"上传日记"))
            pops.add(PopupBean(3,"删除日记"))
            PopupClick(requireActivity(),pops,tv_diary,5).builder().setOnSelectListener{
                when(it.id){
                    1->{
                        if (privacyPassword==null){
                            PrivacyPasswordCreateDialog(requireActivity()).builder().setOnDialogClickListener{
                                privacyPassword=it
                                showToast("日记密码设置成功")
                            }
                        }
                        else{
                            val titleStr=if (privacyPassword?.isSet==true) "确定取消密码？" else "确定设置密码？"
                            CommonDialog(requireActivity()).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    PrivacyPasswordDialog(requireActivity()).builder().setOnDialogClickListener{
                                        privacyPassword!!.isSet=!privacyPassword!!.isSet
                                        MethodManager.savePrivacyPassword(0,privacyPassword)
                                    }
                                }
                            })
                        }
                    }
                    2->{
                        DiaryManageDialog(requireActivity(),1).builder().setOnDialogClickListener{
                            titleStr,startLong,endLong->
                            diaryStartLong=startLong
                            diaryEndLong=endLong
                            diaryUploadTitleStr=titleStr
                            if (NetworkUtil(requireActivity()).isNetworkConnected()){
                                EventBus.getDefault().post(Constants.DIARY_UPLOAD_EVENT)
                            }
                            else{
                                showToast("网络连接失败")
                            }
                        }
                    }
                    3->{
                        DiaryManageDialog(requireActivity(),2).builder().setOnDialogClickListener{
                                titleStr,startLong,endLong->
                            val diarys=DiaryDaoManager.getInstance().queryList(startLong, endLong)
                            for (item in diarys){
                                val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
                                FileUtils.deleteFile(File(path))
                                DiaryDaoManager.getInstance().delete(item)
                            }
                            showToast(2,"删除日记成功")
                        }
                    }
                }
            }
            return@setOnLongClickListener true
        }


        tv_free_note.setOnClickListener {
            customStartActivity(Intent(requireActivity(), FreeNoteActivity::class.java))
        }

        initMessageView()
        initCourse()
        initNoteView()
        initDialog(2)
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            findMessages()
            fetchCommonData()
        }
        findNotes()
    }

    //课程表相关处理
    private fun initCourse() {
//        val path=FileAddress().getPathCourse("course") + "/course0.png"
//        if (File(path).exists()){
//            GlideUtils.setImageNoCacheUrl(activity,path,iv_course)
//        }
//        else{
//            iv_course.setImageResource(0)
//        }
        mPresenter.getClassSchedule()
    }

    private fun initNoteView(){
        rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mNoteAdapter=MainNoteAdapter(R.layout.item_main_note, null).apply {
            rv_main_note.adapter = this
            bindToRecyclerView(rv_main_note)
            setOnItemClickListener { adapter, view, position ->
                MethodManager.gotoNote(requireActivity(),notes[position])
            }
        }
    }


    //消息相关处理
    private fun initMessageView() {
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mMessageAdapter=MainMessageAdapter(R.layout.item_main_message, null).apply {
            rv_main_message.adapter = this
            bindToRecyclerView(rv_main_message)
        }
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=4
        map["type"]=1
        mPresenter.getList(map)
    }

    private fun findNotes(){
        notes = NoteDaoManager.getInstance().queryListOther(6)
        mNoteAdapter?.setNewData(notes)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            COURSE_EVENT -> {
                initCourse()
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
            NOTE_EVENT->{
                findNotes()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }


    /**
     * 每年上传日记
     */
    fun uploadDiary(token:String){
        cloudList.clear()
        val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        if (diarys.isNotEmpty()){
            val paths= mutableListOf<String>()
            for (item in diarys){
                paths.add(FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date)))
            }
            val time=System.currentTimeMillis()
            FileUploadManager(token).apply {
                startUpload(paths,DateUtils.longToString(time))
                setCallBack{
                    cloudList.add(CloudListBean().apply {
                        type=4
                        subTypeStr=diaryUploadTitleStr
                        year=DateUtils.getYear()
                        date=time
                        listJson= Gson().toJson(diarys)
                        downloadUrl=it
                    })
                    mCloudUploadPresenter.upload(cloudList)
                    uploadType=1
                }
            }
        }
    }

    /**
     * 每年上传随笔
     */
    fun uploadFreeNote(token:String){
        cloudList.clear()
        val beans=FreeNoteDaoManager.getInstance().queryListByType()
        val nullItems= mutableListOf<FreeNoteBean>()
        for (item in beans){
            val fileName=DateUtils.longToString(item.date)
            val path=FileAddress().getPathFreeNote(fileName)
            if (FileUtils.isExistContent(path)){
                FileUploadManager(token).apply {
                    startUpload(path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=5
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
        val currentTime=System.currentTimeMillis()
        val itemTypeBean=ItemTypeBean()
        itemTypeBean.title="未分类"+DateUtils.longToStringDataNoYear(currentTime)
        itemTypeBean.date=currentTime
        itemTypeBean.path=FileAddress().getPathScreen("未分类")
        screenTypes.add(0,itemTypeBean)
        for (item in screenTypes){
            val fileName=DateUtils.longToString(item.date)
            val path=item.path
            if (FileUtils.isExistContent(path)){
                FileUploadManager(token).apply {
                    startUpload(path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=6
                            subTypeStr="截图"
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
                val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
                for (item in diarys){
                    val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
                    FileUtils.deleteFile(File(path))
                    DiaryDaoManager.getInstance().delete(item)
                }
                showToast("日记上传成功")
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