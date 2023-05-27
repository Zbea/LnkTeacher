package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteTypeBeanDaoManager
import com.bll.lnkteacher.manager.NotebookDaoManager
import com.bll.lnkteacher.mvp.model.NoteTypeBean
import com.bll.lnkteacher.ui.adapter.NoteBookManagerAdapter
import com.bll.lnkteacher.utils.FileUtils
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class NoteTypeManagerActivity : BaseActivity() {

    private var noteTypes= mutableListOf<NoteTypeBean>()
    private var mAdapter: NoteBookManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_note_type_manager
    }

    override fun initData() {
        noteTypes= NoteTypeBeanDaoManager.getInstance().queryAll()
    }

    override fun initView() {
        setPageTitle("笔记本管理")

        initRecyclerView()
    }


    private fun initRecyclerView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = NoteBookManagerAdapter(R.layout.item_notebook_manager, noteTypes)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNoteBook(noteTypes[position].name)
            }
            if (view.id==R.id.iv_delete){
                setDeleteView()
            }
            if (view.id==R.id.iv_top){
                val date=noteTypes[0].date
                noteTypes[position].date=date-1000
                NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteTypes[position])
                Collections.swap(noteTypes,position,0)
                setNotify()
            }
        }

    }

    //设置刷新通知
    private fun setNotify(){
        mAdapter?.notifyDataSetChanged()
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
        EventBus.getDefault().post(Constants.NOTE_EVENT)//更新全局通知
    }

    //删除
    private fun setDeleteView(){
        CommonDialog(this).setContent("确定要删除该条笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val noteType=noteTypes[position]
                    noteTypes.removeAt(position)
                    //删除笔记本
                    NoteTypeBeanDaoManager.getInstance().deleteBean(noteType)

                    val notebooks=NotebookDaoManager.getInstance().queryAll(noteType.name)
                    //删除该笔记分类中的所有笔记本及其内容
                    for (note in notebooks){
                        NotebookDaoManager.getInstance().deleteBean(note)
                        NoteContentDaoManager.getInstance().deleteType(note.typeStr,note.id)
                        val path= FileAddress().getPathNote(note.typeStr,note.title)
                        FileUtils.deleteFile(File(path))
                    }

                    setNotify()
                }

            })
    }

    //修改笔记本
    private fun editNoteBook(content:String){
        InputContentDialog(this,content).builder().setOnDialogClickListener { string ->
            noteTypes[position].name = string
            NoteTypeBeanDaoManager.getInstance().insertOrReplace(noteTypes[position])
            setNotify()
        }
    }

}