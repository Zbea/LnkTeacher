package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.COURSE_EVENT
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.DiaryManageDialog
import com.bll.lnkteacher.dialog.DiaryUploadListDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.mvp.presenter.MainRightPresenter
import com.bll.lnkteacher.mvp.presenter.SmsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.ISmsView
import com.bll.lnkteacher.ui.activity.MessageListActivity
import com.bll.lnkteacher.ui.activity.ScheduleClassEditActivity
import com.bll.lnkteacher.ui.activity.ScheduleCourseActivity
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
import kotlinx.android.synthetic.main.fragment_main_right.iv_schedule_class
import kotlinx.android.synthetic.main.fragment_main_right.ll_message
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_message
import kotlinx.android.synthetic.main.fragment_main_right.rv_main_note
import kotlinx.android.synthetic.main.fragment_main_right.tv_diary
import kotlinx.android.synthetic.main.fragment_main_right.tv_free_note
import kotlinx.android.synthetic.main.fragment_main_right.tv_schedule_class
import kotlinx.android.synthetic.main.fragment_main_right.tv_schedule_course
import java.io.File

class MainRightFragment : BaseMainFragment(), IContractView.IMainRightView,ISmsView {

    private var smsPresenter=SmsPresenter(this,2)
    private var mPresenter=MainRightPresenter(this,2)
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MainMessageAdapter?=null

    private var notes= mutableListOf<Note>()
    private var mNoteAdapter:MainNoteAdapter?=null
    private var privacyPassword=MethodManager.getPrivacyPassword(0)
    private var diaryStartLong=0L
    private var diaryEndLong=0L
    private var diaryUploadTitleStr=""

    private var privacyPasswordDialog:PrivacyPasswordDialog?=null

    override fun onSms() {
        showToast(2,"短信发送成功")
    }
    override fun onCheckSuccess() {
        showToast(2,"日记密码设置成功")
        MethodManager.savePrivacyPassword(0,privacyPassword)
        privacyPasswordDialog?.getPrivacyPassword()
    }

    override fun onList(message: Message) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }

    override fun onClassSchedule(url: String) {
        GlideUtils.setImageUrl(requireActivity(),url,iv_schedule_class)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_right
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        ll_message.setOnClickListener {
            customStartActivity(Intent(activity, MessageListActivity::class.java))
        }

        tv_schedule_course.setOnClickListener {
            customStartActivity(Intent(activity, ScheduleCourseActivity::class.java))
        }

        tv_schedule_class.setOnClickListener {
            customStartActivity(Intent(activity, ScheduleClassEditActivity::class.java))
        }

        iv_schedule_class.setOnClickListener {
            customStartActivity(Intent(activity, ScheduleClassEditActivity::class.java))
        }

        tv_diary.setOnClickListener {
            startDiaryActivity(0)
        }

        tv_diary.setOnLongClickListener {
            onLongDiary()
            return@setOnLongClickListener true
        }


        tv_free_note.setOnClickListener {
            customStartActivity(Intent(requireActivity(), FreeNoteActivity::class.java))
        }

        initMessageView()
        initNoteView()
        initDialog(2)

    }

    override fun lazyLoad() {
        findNotes()

        if (NetworkUtil.isNetworkConnected()){
            mPresenter.getClassSchedule()
            findMessages()
        }
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

    /**
     * 跳转日记
     */
    private fun startDiaryActivity(typeId:Int){
        if (privacyPassword!=null&&privacyPassword?.isSet==true){
            privacyPasswordDialog=PrivacyPasswordDialog(requireActivity()).builder()
            privacyPasswordDialog?.setOnDialogClickListener(object : PrivacyPasswordDialog.OnDialogClickListener{
                override fun onClick() {
                    customStartActivity(Intent(activity,DiaryActivity::class.java).setFlags(typeId))
                }
                override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                    this@MainRightFragment.privacyPassword=privacyPassword
                    smsPresenter.checkPhone(code)
                }
                override fun onPhone(phone: String) {
                    smsPresenter.sms(phone)
                }
            })
        }
        else{
            customStartActivity(Intent(activity,DiaryActivity::class.java).setFlags(typeId))
        }
    }

    /**
     * 长按日记管理
     */
    private fun onLongDiary(){
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
        pops.add(PopupBean(2,"结集保存"))
        pops.add(PopupBean(3,"云库日记"))
        PopupClick(requireActivity(),pops,tv_diary,0).builder().setOnSelectListener{
            when(it.id){
                1->{
                    if (privacyPassword==null){
                        PrivacyPasswordCreateDialog(requireActivity()).builder().setOnDialogClickListener(object : PrivacyPasswordCreateDialog.OnDialogClickListener {
                            override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                                this@MainRightFragment.privacyPassword=privacyPassword
                                smsPresenter.checkPhone(code)
                            }
                            override fun onPhone(phone: String) {
                                smsPresenter.sms(phone)
                            }
                        })
                    }
                    else{
                        val titleStr=if (privacyPassword?.isSet==true) "确定取消密码？" else "确定设置密码？"
                        CommonDialog(requireActivity(),2).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                privacyPasswordDialog=PrivacyPasswordDialog(requireActivity()).builder()
                                privacyPasswordDialog?.setOnDialogClickListener(object : PrivacyPasswordDialog.OnDialogClickListener{
                                    override fun onClick() {
                                        privacyPassword!!.isSet=!privacyPassword!!.isSet
                                        MethodManager.savePrivacyPassword(0,privacyPassword)
                                    }
                                    override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                                        this@MainRightFragment.privacyPassword=privacyPassword
                                        smsPresenter.checkPhone(code)
                                    }
                                    override fun onPhone(phone: String) {
                                        smsPresenter.sms(phone)
                                    }
                                })
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
                        if (NetworkUtil.isNetworkConnected()){
                            mQiniuPresenter.getToken()
                        }
                        else{
                            showToast("网络连接失败")
                        }
                    }
                }
                3->{
                    DiaryUploadListDialog(requireActivity()).builder().setOnDialogClickListener{ typeId->
                        startDiaryActivity(typeId)
                    }
                }
            }
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
                mPresenter.getClassSchedule()
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
        onCheckUpdate()
        lazyLoad()
    }

    override fun onUpload(token: String){
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
                        title=diaryUploadTitleStr
                        subTypeStr="我的日记"
                        year=DateUtils.getYear()
                        date=time
                        listJson= Gson().toJson(diarys)
                        downloadUrl=it
                    })
                    mCloudUploadPresenter.upload(cloudList)
                }
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        val diarys=DiaryDaoManager.getInstance().queryList(diaryStartLong,diaryEndLong)
        for (item in diarys){
            val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(item.date))
            FileUtils.deleteFile(File(path))
            DiaryDaoManager.getInstance().delete(item)
        }
        showToast("日记上传成功")
    }

}