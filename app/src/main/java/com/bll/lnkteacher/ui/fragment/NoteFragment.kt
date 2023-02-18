package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.dialog.NotebookAddDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.BaseTypeBeanDaoManager
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NotebookDaoManager
import com.bll.lnkteacher.mvp.model.BaseTypeBean
import com.bll.lnkteacher.mvp.model.Notebook
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.activity.NoteDrawingActivity
import com.bll.lnkteacher.ui.activity.NoteTypeManagerActivity
import com.bll.lnkteacher.ui.adapter.NoteTypeAdapter
import com.bll.lnkteacher.ui.adapter.NoteAdapter
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil

/**
 * 笔记
 */
class NoteFragment : BaseFragment() {
    private var popWindowList: PopupRadioList? = null
    private var popupBeans = mutableListOf<PopupBean>()
    private var popWindowMoreBeans = mutableListOf<PopupBean>()
    private var dialog: NoteModuleAddDialog? = null
    private var noteTypes = mutableListOf<BaseTypeBean>()
    private var noteBooks = mutableListOf<Notebook>()
    private var type = 0 //当前笔记本类型
    private var mAdapter: NoteAdapter? = null
    private var mAdapterType: NoteTypeAdapter? = null
    private var position = 0 //当前笔记标记
    private var resId = ""
    private var positionType = 0//当前笔记本标记
    private var isDown = false //是否向下打开

    private var pageIndex=1
    private var pageTotal=1

    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {

        popupBeans.add(PopupBean(0, "新建笔记本", true))
        popupBeans.add(PopupBean(1, "笔记本管理", false))

        popWindowMoreBeans.add(
            PopupBean(
                0,
                "重命名",
                true
            )
        )
        popWindowMoreBeans.add(
            PopupBean(
                1,
                "删除",
                false
            )
        )

        EventBus.getDefault().register(this)

        setTitle("笔记")
        showView(iv_manager)

        bindClick()

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
            val notebook=noteBooks[position]
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

    //设置头部索引
    private fun initTab() {

        rv_type.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
        mAdapterType = NoteTypeAdapter(R.layout.item_bookcase_type, noteTypes)
        rv_type.adapter = mAdapterType
        mAdapterType?.bindToRecyclerView(rv_type)
        mAdapterType?.setOnItemClickListener { adapter, view, position ->
            noteTypes[positionType]?.isCheck = false
            positionType = position
            noteTypes[positionType]?.isCheck = true
            mAdapterType?.notifyDataSetChanged()

            type = noteTypes[positionType]?.typeId
            pageIndex=1
            pageTotal=1
            findDatas()

        }

    }

    private fun bindClick() {

        tv_add.setOnClickListener {
            dialog = NoteModuleAddDialog(requireContext(),type).builder()
            dialog?.setOnDialogClickListener { moduleBean ->
                resId= ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                createNotebook()
            }
        }

        iv_manager?.setOnClickListener {
            setTopSelectView()
        }

        iv_down.setOnClickListener {
            if (isDown) {
                isDown = false
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_down)
            } else {
                isDown = true
                iv_down.setImageResource(R.mipmap.icon_bookstore_arrow_up)
            }
            findTabs()
        }


        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findDatas()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1
                findDatas()
            }
        }

    }

    /**
     * tab数据设置
     */
    private fun findTabs() {

        noteTypes = DataBeanManager.noteBook
        noteTypes.addAll( BaseTypeBeanDaoManager.getInstance().queryAll())
        setAllCheckFalse(noteTypes)

        //删除tab后当前下标超出
        if (noteTypes.size<=positionType){
            positionType = 0
        }

        //不展开 下标超过4
        if (!isDown&&positionType>4){
            positionType = 0
        }

        noteTypes[positionType].isCheck = true
        if (!isDown&&noteTypes.size>5){
            noteTypes = noteTypes.subList(0, 5)
        }

        mAdapterType?.setNewData(noteTypes)
        type = noteTypes[positionType].typeId
        findDatas()
    }

    /**
     * 笔记本数据
     */
    private fun findDatas() {
        noteBooks = NotebookDaoManager.getInstance().queryAll(type,pageIndex,10)
        val total= NotebookDaoManager.getInstance().queryAll(type)
        pageTotal= ceil((total.size.toDouble()/10)).toInt()
        mAdapter?.setNewData(noteBooks)
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageTotal.toString()
    }

    //设置所有数据为不选中
    private fun setAllCheckFalse(tabs:List<BaseTypeBean>){
        for (item in tabs){
            item.isCheck=false
        }
    }

    /**
     * 跳转笔记写作
     */
    private fun gotoIntent(note: Notebook){
        var intent = Intent(activity, NoteDrawingActivity::class.java)
        var bundle = Bundle()
        bundle.putSerializable("note", note)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }



    //新建笔记
    private fun createNotebook() {
        var note = Notebook()
        NotebookAddDialog(requireContext(), "新建笔记本", "", "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                note.title = string
                note.createDate = System.currentTimeMillis()
                note.type = type
                note.contentResId=resId
                if (noteBooks.size==10)
                    pageIndex+=1
                NotebookDaoManager.getInstance().insertOrReplace(note)
                EventBus.getDefault().post(NOTE_EVENT)
            }
    }

    //修改笔记
    private fun editNotebook(content: String) {
        NotebookAddDialog(requireContext(), "重命名", content, "请输入笔记标题").builder()
            ?.setOnDialogClickListener { string ->
                noteBooks[position].title = string
                mAdapter?.notifyDataSetChanged()
                NotebookDaoManager.getInstance().insertOrReplace(noteBooks[position])
                EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
            }
    }

    //删除
    private fun deleteNotebook() {
        CommonDialog(activity).setContent("确定要删除笔记本？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    var note = noteBooks[position]
                    noteBooks.removeAt(position)
                    mAdapter?.notifyDataSetChanged()
                    //删除笔记本
                    NotebookDaoManager.getInstance().deleteBean(note)
                    //删除笔记本中的所有笔记
                    NoteContentDaoManager.getInstance().deleteType(note.type,note.id)
                    EventBus.getDefault().post(NOTE_EVENT)//更新全局通知
                }

            })
    }


    //顶部弹出选择
    private fun setTopSelectView() {
        if (popWindowList == null) {
            popWindowList = PopupRadioList(requireActivity(), popupBeans, iv_manager, 20).builder()
            popWindowList?.setOnSelectListener { item ->
                if (item.id == 0) {
                    addNoteBookType()
                } else {
                    startActivity(Intent(activity, NoteTypeManagerActivity::class.java))
                }
            }
        } else {
            popWindowList?.show()
        }
    }


    //新建笔记分类
    private fun addNoteBookType() {
        NotebookAddDialog(requireContext(),"新建笔记分类", "", "输入笔记分类").builder()
            ?.setOnDialogClickListener { string ->
                var noteBook = BaseTypeBean()
                noteBook.name = string
                noteBook.typeId = noteTypes.size
                noteTypes.add(noteBook)
                BaseTypeBeanDaoManager.getInstance().insertOrReplace(noteBook)
                mAdapterType?.notifyDataSetChanged()
            }
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == NOTE_BOOK_MANAGER_EVENT) {
            findTabs()
        }
        if (msgFlag == NOTE_EVENT) {
            findDatas()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}