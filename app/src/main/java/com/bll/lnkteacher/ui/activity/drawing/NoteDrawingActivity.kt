package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.NoteContent
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class NoteDrawingActivity : BaseDrawingActivity() {

    private var typeStr = ""
    private var note: Note? = null
    private var note_Content_b: NoteContent? = null//当前内容
    private var note_Content_a: NoteContent? = null//a屏内容
    private var noteContents = mutableListOf<NoteContent>() //所有内容
    private var page = 0//页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id = intent.getLongExtra("noteId",0)
        note= NoteDaoManager.getInstance().queryBean(id)
        typeStr = note?.typeStr.toString()

        noteContents = NoteContentDaoManager.getInstance().queryAll(typeStr, note?.title)

        if (noteContents.isNotEmpty()) {
            note_Content_b = noteContents[noteContents.size - 1]
            page = note?.page!!
        } else {
            newNoteContent()
        }
    }


    override fun initView() {
        disMissView(iv_btn)
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this,note?.contentResId),v_content_a)
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this,note?.contentResId),v_content_b)
        onChangeContent()
    }


    override fun onCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in noteContents) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = item.page
            itemList.isEdit=true
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,true).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (page!=pageNumber){
                    page = pageNumber
                    onChangeContent()
                }
            }
            override fun onEdit(title: String, pages: List<Int>) {
                for (page in pages){
                    val item=noteContents[page]
                    item.title=title
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(item)
                }
            }
        })
    }

    override fun onPageDown() {
        val total = noteContents.size - 1
        if(isExpand){
            if (page<total-1){
                page+=2
                onChangeContent()
            }
            else if (page==total-1){
                if (isDrawLastContent()){
                    newNoteContent()
                    page=noteContents.size-1
                    onChangeContent()
                }
                else{
                    page=total
                    onChangeContent()
                }
            }
        }
        else{
            if (page ==total) {
                if (isDrawLastContent()){
                    newNoteContent()
                    page=noteContents.size-1
                    onChangeContent()
                }
            } else {
                page += 1
                onChangeContent()
            }
        }
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                onChangeContent()
            } else if (page == 2) {
                page = 1
                onChangeContent()
            }
        } else {
            if (page > 0) {
                page -= 1
                onChangeContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        if (noteContents.size==1){
            //如果最后一张已写,则可以在全屏时创建新的
            if (isDrawLastContent()){
                newNoteContent()
            }
            else{
                return
            }
        }
        if (page==0){
            page=1
        }
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    /**
     * 最后一个是否已写
     */
    private fun isDrawLastContent():Boolean{
        val contentBean = noteContents.last()
        return File(contentBean.filePath).exists()
    }

    override fun onChangeContent() {
        note_Content_b = noteContents[page]
        if (isExpand) {
            note_Content_a = noteContents[page-1]
        }

        tv_page_total.text="${noteContents.size}"
        tv_page_total_a.text="${noteContents.size}"

        setElikLoadPath(elik_b!!, note_Content_b!!.filePath)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, note_Content_a!!.filePath)
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
            }
            else{
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    //创建新的作业内容
    private fun newNoteContent() {
        val date = System.currentTimeMillis()
        val path = FileAddress().getPathNote(typeStr, note?.title)
        val pathName = DateUtils.longToString(date)

        note_Content_b = NoteContent()
        note_Content_b?.date = date
        note_Content_b?.typeStr = typeStr
        note_Content_b?.noteTitle = note?.title
        note_Content_b?.resId = note?.contentResId
        note_Content_b?.title = "未命名${noteContents.size + 1}"
        note_Content_b?.filePath = "$path/$pathName.png"
        note_Content_b?.pathName = pathName
        note_Content_b?.page = noteContents.size
        page = noteContents.size

        NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_b)
        val id = NoteContentDaoManager.getInstance().insertId
        note_Content_b?.id = id

        noteContents.add(note_Content_b!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        note?.page=page
        NoteDaoManager.getInstance().insertOrReplace(note)
    }

}