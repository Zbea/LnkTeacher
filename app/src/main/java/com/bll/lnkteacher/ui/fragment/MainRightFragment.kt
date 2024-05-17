package com.bll.lnkteacher.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.COURSE_EVENT
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.manager.FreeNoteDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.presenter.MainPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.*
import com.bll.lnkteacher.ui.activity.drawing.DiaryActivity
import com.bll.lnkteacher.ui.activity.drawing.FreeNoteActivity
import com.bll.lnkteacher.ui.adapter.MainMessageAdapter
import com.bll.lnkteacher.ui.adapter.MainNoteAdapter
import com.bll.lnkteacher.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main_right.*
import java.io.File
import java.util.*

class MainRightFragment : BaseMainFragment(),IContractView.IMainView {

    private var mPresenter=MainPresenter(this,2)
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MainMessageAdapter?=null

    private var notes= mutableListOf<Note>()
    private var mNoteAdapter:MainNoteAdapter?=null

    private var uploadType=0//上传类型
    private var privacyPassword:PrivacyPassword?=null

    override fun onList(message: Message) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }

    override fun onListFriend(list: FriendList) {
        DataBeanManager.friends=list.list
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
            customStartActivity(Intent(activity, MainCourseActivity::class.java).setFlags(1)
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
        if (NetworkUtil.isNetworkAvailable(requireActivity())){
            findMessages()
            mPresenter.getFriends()
            fetchCommonData()
        }
        findNotes()
    }

    //课程表相关处理
    private fun initCourse() {
        val path=FileAddress().getPathCourse("course") + "/course0.png"
        if (File(path).exists()){
            GlideUtils.setImageNoCacheUrl(activity,path,iv_course)
        }
        else{
            iv_course.setImageResource(0)
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
        itemTypeBean.title="全部"
        itemTypeBean.date=System.currentTimeMillis()
        itemTypeBean.path=FileAddress().getPathScreen("未分类")
        screenTypes.add(0,itemTypeBean)
        for (item in screenTypes){
            val fileName=DateUtils.longToString(item.date)
            val path=item.path
            if (!FileUtils.getFiles(path).isNullOrEmpty()){
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