package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ModuleItemDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordCreateDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.mvp.presenter.SmsPresenter
import com.bll.lnkteacher.mvp.view.IContractView.ISmsView
import com.bll.lnkteacher.ui.activity.NotebookManagerActivity
import com.bll.lnkteacher.ui.adapter.NoteAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.fragment_list.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseMainFragment(), ISmsView {
    private var smsPresenter= SmsPresenter(this,2)

    private var popupBeans = mutableListOf<PopupBean>()
    private var notes = mutableListOf<Note>()
    private var mAdapter: NoteAdapter? = null
    private var position = 0 //当前笔记标记
    private var tabPos = 0//当前笔记本标记
    private var typeStr=""
    private var privacyPassword:PrivacyPassword?=null
    private var privacyPasswordSave:PrivacyPassword?=null
    private var privacyPasswordDialog:PrivacyPasswordDialog?=null

    override fun onSms() {
        showToast(2,"短信发送成功")
    }
    override fun onCheckSuccess() {
        showToast(2,"密本密码设置成功")
        privacyPassword=privacyPasswordSave
        MethodManager.savePrivacyPassword(1,privacyPasswordSave)
        privacyPasswordDialog?.getPrivacyPassword()
        mAdapter?.notifyItemChanged(position)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list
    }

    override fun initView() {
        pageSize=14

        popupBeans.add(PopupBean(0, "管理笔记本", true))
        popupBeans.add(PopupBean(1, "创建笔记本", false))

        setTitle(DataBeanManager.getIndexRightData()[4].name)
        showView(iv_manager)

        privacyPassword=MethodManager.getPrivacyPassword(1)

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> startActivity(Intent(activity, NotebookManagerActivity::class.java))
                        1 -> createNoteBook()
                    }
                }
        }

        initRecyclerView()
        initTabs()
    }

    override fun lazyLoad() {
    }

    /**
     * tab数据设置
     */
    private fun initTabs() {
        pageIndex=1
        itemTabTypes=ItemTypeDaoManager.getInstance().queryAll(1)
        itemTabTypes.add(0,ItemTypeBean().apply {
            title = getString(R.string.note_tab_diary)
        })
        if (tabPos>=itemTabTypes.size){
            tabPos=0
        }
        itemTabTypes=MethodManager.setItemTypeBeanCheck(itemTabTypes,tabPos)
        typeStr = itemTabTypes[tabPos].title
        mTabTypeAdapter?.setNewData(itemTabTypes)

        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos=position
        typeStr=itemTabTypes[position].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(0, DP2PX.dip2px(requireActivity(),30f), 0,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NoteAdapter(R.layout.item_note, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val note = notes[position]
            if (tabPos==0&&privacyPassword!=null&&!note.isCancelPassword){
                privacyPasswordDialog=PrivacyPasswordDialog(requireActivity(),1).builder()
                privacyPasswordDialog?.setOnDialogClickListener(object : PrivacyPasswordDialog.OnDialogClickListener{
                    override fun onClick() {
                        MethodManager.gotoNote(requireActivity(),note)
                    }
                    override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                        privacyPasswordSave=privacyPassword
                        smsPresenter.checkPhone(code)
                    }
                    override fun onPhone(phone: String) {
                        smsPresenter.sms(phone)
                    }
                })
            }
            else{
                MethodManager.gotoNote(requireActivity(),note)
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            val note=notes[position]
            when (view.id) {
                R.id.iv_delete -> {
                    CommonDialog(requireActivity()).setContent("确定删除${note.title}？").builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteNote()
                            }
                        })
                }
                R.id.iv_edit -> {
                    InputContentDialog(requireContext(),2, note.title).builder()
                        .setOnDialogClickListener { string ->
                            if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                                showToast(R.string.toast_existed)
                                return@setOnDialogClickListener
                            }
                            //修改内容分类
                            NoteContentDaoManager.getInstance().editNoteTitles(note.typeStr,note.title,string)
                            note.title = string
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            mAdapter?.notifyItemChanged(position)
                        }
                }
                R.id.iv_password->{
                    if (privacyPassword==null){
                        PrivacyPasswordCreateDialog(requireActivity()).builder().setOnDialogClickListener(object : PrivacyPasswordCreateDialog.OnDialogClickListener {
                            override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                                privacyPasswordSave=privacyPassword
                                smsPresenter.checkPhone(code)
                            }
                            override fun onPhone(phone: String) {
                                smsPresenter.sms(phone)
                            }
                        })
                    }
                    else{
                        val titleStr=if (note.isCancelPassword) "确定设置密码？" else "确定取消密码？"
                        CommonDialog(requireActivity()).setContent(titleStr).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                privacyPasswordDialog=PrivacyPasswordDialog(requireActivity(),1).builder()
                                privacyPasswordDialog?.setOnDialogClickListener(object : PrivacyPasswordDialog.OnDialogClickListener{
                                    override fun onClick() {
                                        note.isCancelPassword=!note.isCancelPassword
                                        NoteDaoManager.getInstance().insertOrReplace(note)
                                        mAdapter?.notifyItemChanged(position)
                                    }
                                    override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                                        privacyPasswordSave=privacyPassword
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
                R.id.iv_upload->{
                    val path=FileAddress().getPathNote(note.typeStr,note.title)
                    if (!FileUtils.isExistContent(path)){
                        showToast("${note.title}暂无内容，无需上传")
                        return@setOnItemChildClickListener
                    }
                    CommonDialog(requireActivity()).setContent("上传${note.title}到云书库？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            mQiniuPresenter.getToken()
                        }
                    })
                }
            }
        }
        val view =requireActivity().layoutInflater.inflate(R.layout.common_add_view,null)
        view.setOnClickListener {
            ModuleItemDialog(requireContext(), 3,"笔记模板",DataBeanManager.noteModuleBook).builder()
                .setOnDialogClickListener { moduleBean ->
                    createNote(ToolUtils.getImageResStr(activity, moduleBean.resContentId))
                }
        }
        mAdapter?.addFooterView(view)
    }

    //新建笔记本
    private fun createNoteBook() {
        InputContentDialog(requireContext(),  2,"请输入笔记本").builder()
            .setOnDialogClickListener { string ->
                if (ItemTypeDaoManager.getInstance().isExist(string,1)){
                    showToast(R.string.toast_existed)
                }
                else{
                    val noteBook = ItemTypeBean()
                    noteBook.type=1
                    noteBook.title = string
                    noteBook.date=System.currentTimeMillis()
                    ItemTypeDaoManager.getInstance().insertOrReplace(noteBook)
                    mTabTypeAdapter?.addData(noteBook)
                }
            }
    }

    //新建主题
    private fun createNote(resId:String) {
        val note = Note()
        InputContentDialog(requireContext(),  2,"请输入主题").builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.date = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId = resId
                NoteDaoManager.getInstance().insertOrReplace(note)
                if (notes.size==10){
                    pageIndex+=1
                    EventBus.getDefault().post(NOTE_EVENT)
                }
                else{
                    mAdapter?.addData(0,note)
                }
            }
    }

    private fun deleteNote(){
        val note=notes[position]
        //删除主题
        NoteDaoManager.getInstance().deleteBean(note)
        //删除主题内容
        NoteContentDaoManager.getInstance().deleteType(note.typeStr, note.title)
        val path= FileAddress().getPathNote(note.typeStr,note.title)
        FileUtils.deleteFile(File(path))

        mAdapter?.remove(position)

        if (notes.size==0){
            if (pageIndex>1){
                pageIndex-=1
                fetchData()
            }
            else{
                setPageNumber(0)
            }
        }
    }


    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr, pageIndex, pageSize)
        val total = NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(notes)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            NOTE_BOOK_MANAGER_EVENT->{
                initTabs()
            }
            NOTE_EVENT->{
                fetchData()
            }
        }
    }

    override fun onUpload(token: String) {
        cloudList.clear()
        val note=notes[position]
        val path=FileAddress().getPathNote(note.typeStr,note.title)
        //获取笔记所有内容
        val noteContents = NoteContentDaoManager.getInstance().queryAll(note.typeStr,note.title)
        FileUploadManager(token).apply {
            startUpload(path,note.title)
            setCallBack{
                cloudList.add(CloudListBean().apply {
                    type=3
                    title=note.title
                    subTypeStr=note.typeStr
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(note)
                    contentJson= Gson().toJson(noteContents)
                    downloadUrl=it
                })
                mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        deleteNote()
    }

}