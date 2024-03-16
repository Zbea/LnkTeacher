package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.Constants.Companion.PRIVACY_PASSWORD_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.dialog.PrivacyPasswordDialog
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.ui.activity.NotebookManagerActivity
import com.bll.lnkteacher.ui.adapter.NoteAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseMainFragment() {
    private var popupBeans = mutableListOf<PopupBean>()
    private var notebooks = mutableListOf<ItemTypeBean>()
    private var notes = mutableListOf<Note>()
    private var mAdapter: NoteAdapter? = null
    private var position = 0 //当前笔记标记
    private var positionType = 0//当前笔记本标记
    private var typeStr=""
    private var privacyPassword:PrivacyPassword?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        pageSize=10

        popupBeans.add(PopupBean(0, "笔记本管理", true))
        popupBeans.add(PopupBean(1, "新建笔记本", false))

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        privacyPassword=MethodManager.getPrivacyPassword()

        iv_manager?.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 20).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> startActivity(Intent(activity, NotebookManagerActivity::class.java))
                        1 -> createNoteBookType()
                    }
                }
        }

        initRecyclerView()
        findTabs()
    }

    override fun lazyLoad() {
    }


    private fun initRecyclerView() {
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = NoteAdapter(R.layout.item_note, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val note = notes[position]
            if (positionType==0&&privacyPassword!=null&&privacyPassword?.isSet==true&&!note.isCancelPassword){
                PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                    MethodManager.gotoNote(requireActivity(),note)
                }
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
                    CommonDialog(requireActivity()).setContent("确定要删除主题？").builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                //删除笔记本
                                NoteDaoManager.getInstance().deleteBean(note)
                                //删除笔记本中的所有笔记
                                NoteContentDaoManager.getInstance().deleteType(note.typeStr, note.title)
                                val path= FileAddress().getPathNote(note.typeStr,note.title)
                                FileUtils.deleteFile(File(path))

                                notes.remove(note)
                                if (pageIndex>1&&notes.size==0){
                                    pageIndex-=1
                                }
                                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                            }
                        })
                }
                R.id.iv_edit -> {
                    InputContentDialog(requireContext(), note.title).builder()
                        .setOnDialogClickListener { string ->
                            if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                                showToast(R.string.toast_existed)
                                return@setOnDialogClickListener
                            }
                            //修改内容分类
                            NoteContentDaoManager.getInstance().editNotes(note.typeStr,note.title,string)
                            note.title = string
                            NoteDaoManager.getInstance().insertOrReplace(note)
                            EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                        }
                }
                R.id.iv_password->{
                    PrivacyPasswordDialog(requireActivity()).builder()?.setOnDialogClickListener{
                        note.isCancelPassword=!note.isCancelPassword
                        mAdapter?.notifyItemChanged(position)
                        NoteDaoManager.getInstance().insertOrReplace(note)
                    }
                }
            }
        }
        val view =requireActivity().layoutInflater.inflate(R.layout.common_add_view,null)
        view.setOnClickListener {
            NoteModuleAddDialog(requireContext(), 1).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    createNote(ToolUtils.getImageResStr(activity, moduleBean.resContentId))
                }
        }
        mAdapter?.addFooterView(view)
    }

    /**
     * tab数据设置
     */
    private fun findTabs() {
        notebooks=DataBeanManager.notebooks
        notebooks.addAll(ItemTypeDaoManager.getInstance().queryAll(1))
        if (positionType>=notebooks.size){
            positionType=0
        }
        typeStr = notebooks[positionType].title
        initTab()
        fetchData()
    }

    //设置头部索引
    private fun initTab() {
        rg_group.removeAllViews()
        for (i in notebooks.indices) {
            rg_group.addView(getRadioButton(i,positionType, notebooks[i].title, notebooks.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            positionType=id
            typeStr=notebooks[positionType].title
            pageIndex=1
            fetchData()
        }
    }

    //新建笔记
    private fun createNote(resId:String) {
        val note = Note()
        InputContentDialog(requireContext(),  "请输入主题").builder()
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

                if(notes.size==10){
                    pageIndex+=1
                }
                EventBus.getDefault().post(NOTE_EVENT)
            }
    }


    //新建笔记分类
    private fun createNoteBookType() {
        InputContentDialog(requireContext(),  "请输入笔记本").builder()
            .setOnDialogClickListener { string ->
                if (ItemTypeDaoManager.getInstance().isExist(string,1)){
                    showToast(R.string.toast_existed)
                }
                else{
                    val noteBook = ItemTypeBean()
                    noteBook.type=1
                    noteBook.title = string
                    noteBook.date=System.currentTimeMillis()
                    notebooks.add(noteBook)
                    ItemTypeDaoManager.getInstance().insertOrReplace(noteBook)
                    initTab()
                }
            }
    }

    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr, pageIndex, pageSize)
        val total = NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        for (item in notes){
            item.isSet = positionType==0&&privacyPassword!=null&&privacyPassword?.isSet==true
        }
        mAdapter?.setNewData(notes)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            NOTE_BOOK_MANAGER_EVENT->{
                findTabs()
            }
            NOTE_EVENT->{
                fetchData()
            }
            PRIVACY_PASSWORD_EVENT->{
                privacyPassword=MethodManager.getPrivacyPassword()
                fetchData()
            }
        }
    }

    /**
     * 上传
     */
    fun upload(token:String){
        cloudList.clear()
        val nullItems= mutableListOf<Note>()
        for (noteType in notebooks){
            //查找到这个分类的所有内容，然后遍历上传所有内容
            val notes= NoteDaoManager.getInstance().queryAll(noteType.title)
            for (item in notes){
                val path=FileAddress().getPathNote(noteType.title,item.title)
                val fileName=item.title
                //获取笔记所有内容
                val noteContents = NoteContentDaoManager.getInstance().queryAll(item.typeStr,item.title)
                //如果此笔记还没有开始书写，则不用上传源文件
                if (noteContents.size>0){
                    FileUploadManager(token).apply {
                        startUpload(path,fileName)
                        setCallBack{
                            cloudList.add(CloudListBean().apply {
                                type=3
                                subType=-1
                                subTypeStr=item.typeStr
                                year= DateUtils.getYear()
                                date=System.currentTimeMillis()
                                listJson= Gson().toJson(item)
                                contentJson= Gson().toJson(noteContents)
                                downloadUrl=it
                            })
                            //当加入上传的内容等于全部需要上传时候，则上传
                            if (cloudList.size== NoteDaoManager.getInstance().queryAll().size-nullItems.size)
                                mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
                else{
                    nullItems.add(item)
                }
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (i in notebooks.indices){
            val notes= NoteDaoManager.getInstance().queryAll(notebooks[i].title)
            //删除该笔记分类中的所有笔记本及其内容
            for (note in notes){
                NoteDaoManager.getInstance().deleteBean(note)
                NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.title)
                val path= FileAddress().getPathNote(note.typeStr,note.title)
                FileUtils.deleteFile(File(path))
            }
        }
        ItemTypeDaoManager.getInstance().clear(1)
        EventBus.getDefault().post(NOTE_BOOK_MANAGER_EVENT)
    }

}