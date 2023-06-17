package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.content.Intent
import android.os.Bundle
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
import com.bll.lnkteacher.manager.NoteTypeBeanDaoManager
import com.bll.lnkteacher.manager.NotebookDaoManager
import com.bll.lnkteacher.mvp.model.NoteTypeBean
import com.bll.lnkteacher.mvp.model.Notebook
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.activity.NoteDrawingActivity
import com.bll.lnkteacher.ui.activity.NoteTypeManagerActivity
import com.bll.lnkteacher.ui.adapter.NoteAdapter
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 笔记
 */
class NoteFragment : BaseFragment() {
    private var popWindowList: PopupClick? = null
    private var popupBeans = mutableListOf<PopupBean>()
    private var popWindowMoreBeans = mutableListOf<PopupBean>()
    private var noteTypes = mutableListOf<NoteTypeBean>()
    private var noteBooks = mutableListOf<Notebook>()
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

        popWindowMoreBeans.add(PopupBean(0, "重命名", true))
        popWindowMoreBeans.add(PopupBean(1, "删除", false))

        EventBus.getDefault().register(this)

        setTitle(R.string.main_note_title)
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            setTopSelectView()
        }

        initTab()

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
            val notebook = noteBooks[position]
            gotoIntent(notebook)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            if (view.id == R.id.iv_delete) {
                deleteNotebook()
            }
            if (view.id == R.id.iv_edit) {
                editNotebook(noteBooks[position].title)
            }

        }
    }

    /**
     * tab数据设置
     */
    private fun findTabs() {
        noteTypes = DataBeanManager.noteBook
        noteTypes.addAll(NoteTypeBeanDaoManager.getInstance().queryAll())
        if (positionType>noteTypes.size){
            positionType=0
        }
        typeStr = noteTypes[positionType].name
        initTab()
        fetchData()
    }

    //设置头部索引
    private fun initTab() {
        rg_group.removeAllViews()
        for (i in noteTypes.indices) {
            rg_group.addView(getRadioButton(i,positionType, noteTypes[i].name, noteTypes.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            positionType=id
            typeStr=noteTypes[positionType].name
            pageIndex=1
            fetchData()
        }
    }


    /**
     * 跳转笔记写作
     */
    private fun gotoIntent(note: Notebook) {
        val intent = Intent(activity, NoteDrawingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("noteBundle",note)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }


    //新建笔记
    private fun createNotebook(resId:String) {
        val note = Notebook()
        InputContentDialog(requireContext(),  "请输入笔记本").builder()
            .setOnDialogClickListener { string ->
                if (NotebookDaoManager.getInstance().isExist(typeStr,string)){
                    showToast(R.string.toast_existed)
                    return@setOnDialogClickListener
                }
                note.title = string
                note.createDate = System.currentTimeMillis()
                note.typeStr = typeStr
                note.contentResId = resId
                if (noteBooks.size == 10)
                    pageIndex += 1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
    }

    //修改笔记
    private fun editNotebook(content: String) {
        InputContentDialog(requireContext(), content).builder()
            .setOnDialogClickListener { string ->
                noteBooks[position].title = string
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(noteBooks[position])
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun deleteNotebook() {
        CommonDialog(requireActivity()).setContent("确定要删除笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    val note = noteBooks[position]
                    noteBooks.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    //删除笔记本
                    NotebookDaoManager.getInstance().deleteBean(note)
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
                    0 -> startActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                    1 -> addNoteBookType()
                    else -> {
                        NoteModuleAddDialog(requireContext(), if (typeStr=="我的日记") 0 else 1).builder()
                            ?.setOnDialogClickListener { moduleBean ->
                                createNotebook(ToolUtils.getImageResStr(activity, moduleBean.resContentId))
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
        InputContentDialog(requireContext(),  "请输入笔记本分类").builder()
            .setOnDialogClickListener { string ->
                if (NoteTypeBeanDaoManager.getInstance().isExist(string)){
                    showToast(R.string.toast_existed)
                }
                else{
                    val noteBook = NoteTypeBean()
                    noteBook.name = string
                    noteBook.typeId = System.currentTimeMillis().toInt()
                    noteTypes.add(noteBook)
                    NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteBook)
                    initTab()
                }

            }
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == NOTE_BOOK_MANAGER_EVENT) {
            findTabs()
        }
        if (msgFlag == NOTE_EVENT) {
            fetchData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(typeStr, pageIndex, 10)
        val total = NotebookDaoManager.getInstance().queryAll(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(noteBooks)
    }

}