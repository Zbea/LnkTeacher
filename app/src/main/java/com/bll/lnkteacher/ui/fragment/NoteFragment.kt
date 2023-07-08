package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.manager.NotebookDaoManager
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.Notebook
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.activity.NotebookManagerActivity
import com.bll.lnkteacher.ui.adapter.NoteAdapter
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseFragment() {
    private var popWindowList: PopupClick? = null
    private var popupBeans = mutableListOf<PopupBean>()
    private var notebooks = mutableListOf<Notebook>()
    private var notes = mutableListOf<Note>()
    private var mAdapter: NoteAdapter? = null
    private var position = 0 //当前笔记标记
    private var positionType = 0//当前笔记本标记
    private var typeStr=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        pageSize=10

        popupBeans.add(PopupBean(0, "笔记本管理", true))
        popupBeans.add(PopupBean(1, "新建笔记本", false))
        popupBeans.add(PopupBean(2, "新建笔记", false))

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            setTopSelectView()
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
            gotoNote(note)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            if (view.id == R.id.iv_delete) {
                deleteNote()
            }
            if (view.id == R.id.iv_edit) {
                editNote(notes[position].title)
            }

        }
    }

    /**
     * tab数据设置
     */
    private fun findTabs() {
        notebooks=DataBeanManager.notebooks
        notebooks.addAll(NotebookDaoManager.getInstance().queryAll())
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
        InputContentDialog(requireContext(),  "请输入笔记").builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.date = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId = resId
                pageIndex=1
                NoteDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
    }

    //修改笔记
    private fun editNote(content: String) {
        InputContentDialog(requireContext(), content).builder()
            .setOnDialogClickListener { string ->
                if (NoteDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                val note=notes[position]
                //修改内容分类
                NoteContentDaoManager.getInstance().editNotes(note.typeStr,note.id,string)
                note.title = string
                mAdapter?.notifyDataSetChanged()
                NoteDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun deleteNote() {
        CommonDialog(requireActivity()).setContent("确定要删除笔记？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    val note = notes[position]
                    notes.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    //删除笔记本
                    NoteDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.typeStr, note.id)
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                    val path= FileAddress().getPathNote(note.typeStr,note.title)
                    FileUtils.deleteFile(File(path))
                }

            })
    }


    //顶部弹出选择
    private fun setTopSelectView() {
        if (popWindowList == null) {
            popWindowList = PopupClick(requireActivity(), popupBeans, iv_manager, 20).builder()
            popWindowList?.setOnSelectListener { item ->
                when (item.id) {
                    0 -> startActivity(Intent(activity, NotebookManagerActivity::class.java))
                    1 -> addNoteBookType()
                    else -> {
                        NoteModuleAddDialog(requireContext(), if (typeStr==resources.getString(R.string.note_tab_diary)) 0 else 1).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                createNote(ToolUtils.getImageResStr(activity, moduleBean.resContentId))
                            }
                    }

                }

            }
        } else {
            popWindowList?.show()
        }
    }


    //新建笔记分类
    private fun addNoteBookType() {
        InputContentDialog(requireContext(),  "请输入笔记本").builder()
            .setOnDialogClickListener { string ->
                if (NotebookDaoManager.getInstance().isExist(string)){
                    showToast(R.string.toast_existed)
                }
                else{
                    val noteBook = Notebook()
                    noteBook.title = string
                    noteBook.date=System.currentTimeMillis()
                    noteBook.typeId=ToolUtils.getDateId()
                    notebooks.add(noteBook)
                    NotebookDaoManager.getInstance().insertOrReplace(noteBook)
                    initTab()
                }
            }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            NOTE_BOOK_MANAGER_EVENT->{
                findTabs()
            }
            NOTE_EVENT->{
                fetchData()
            }
        }
    }

    override fun fetchData() {
        notes = NoteDaoManager.getInstance().queryAll(typeStr, pageIndex, pageSize)
        val total = NoteDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(notes)
    }

}