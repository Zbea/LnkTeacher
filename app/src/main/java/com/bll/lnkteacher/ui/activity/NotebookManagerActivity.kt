package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.adapter.ItemTypeManagerAdapter
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list
import org.greenrobot.eventbus.EventBus

class NotebookManagerActivity : BaseAppCompatActivity() {

    private var noteBooks= mutableListOf<ItemTypeBean>()
    private var mAdapter: ItemTypeManagerAdapter? = null
    private var position=0

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        noteBooks= ItemTypeDaoManager.getInstance().queryAll(1)
    }

    override fun initView() {
        setPageTitle("管理笔记本")

        initRecyclerView()
    }


    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f),
            DP2PX.dip2px(this,100f),DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ItemTypeManagerAdapter(R.layout.item_notebook_manager, noteBooks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.iv_edit){
                editNoteBook(noteBooks[position].title)
            }
            if (view.id==R.id.iv_delete){
                deleteNotebook()
            }
            if (view.id==R.id.iv_top){
                val date=noteBooks[0].date//拿到最小时间
                noteBooks[position].date=date-1000
                ItemTypeDaoManager.getInstance().insertOrReplace(noteBooks[position])
                noteBooks.sortWith(Comparator { item1, item2 ->
                    return@Comparator item1.date.compareTo(item2.date)
                })
                setNotify()
            }
        }

    }

    //设置刷新通知
    private fun setNotify(){
        mAdapter?.notifyDataSetChanged()
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
    }

    //删除
    private fun deleteNotebook(){
        CommonDialog(this).setContent("确定删除笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val noteType=noteBooks[position]
                    val notes= NoteDaoManager.getInstance().queryAll(noteType.title)
                    if (notes.isNotEmpty()){
                        showToast("笔记本存在内容,无法删除")
                    }
                    else{
                        noteBooks.removeAt(position)
                        ItemTypeDaoManager.getInstance().deleteBean(noteType)
                        setNotify()
                    }
                }
            })
    }

    //修改笔记本
    private fun editNoteBook(content:String){
        InputContentDialog(this,content).builder().setOnDialogClickListener { string ->
            if (ItemTypeDaoManager.getInstance().isExist(string,1)){
                showToast(R.string.toast_existed)
                return@setOnDialogClickListener
            }
            val noteBook=noteBooks[position]
            //修改笔记、内容分类
            val notes=NoteDaoManager.getInstance().queryAll(noteBook.title)
            for (note in notes){
                NoteContentDaoManager.getInstance().editNoteTypes(noteBook.title,note.title,string)
            }
            NoteDaoManager.getInstance().editNotes(noteBook.title,string)

            noteBook.title = string
            ItemTypeDaoManager.getInstance().insertOrReplace(noteBook)

            setNotify()
        }
    }

}