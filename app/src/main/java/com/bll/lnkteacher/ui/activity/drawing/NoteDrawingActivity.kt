package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.DrawingCatalogDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.NoteContent
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

class NoteDrawingActivity : BaseDrawingActivity() {

    private var typeStr = ""
    private var note: Note? = null
    private var noteContent: NoteContent? = null//当前内容
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

        if (noteContents.size > 0) {
            noteContent = noteContents[noteContents.size - 1]
            page = note?.page!!
        } else {
            newNoteContent()
        }
    }


    override fun initView() {
        disMissView(iv_btn)
        v_content_a.setImageResource(ToolUtils.getImageResId(this, note?.contentResId))//设置背景
        v_content_b.setImageResource(ToolUtils.getImageResId(this, note?.contentResId))//设置背景
        onChangeContent()

        tv_page_a.setOnClickListener {
            InputContentDialog(this, 2, noteContent?.title!!).builder().setOnDialogClickListener { string ->
                noteContent?.title = string
                noteContents[page].title = string
                NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
            }
        }

        tv_page.setOnClickListener {
            if (isExpand){
                InputContentDialog(this, 1, note_Content_a?.title!!).builder().setOnDialogClickListener { string ->
                    note_Content_a?.title = string
                    noteContents[page-1].title = string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(note_Content_a)
                }
            }
            else{
                InputContentDialog(this, 1, noteContent?.title!!).builder().setOnDialogClickListener { string ->
                    noteContent?.title = string
                    noteContents[page].title = string
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                }
            }

        }
    }


    override fun onCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in noteContents) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = item.page
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }

        }
        DrawingCatalogDialog(this, list).builder().setOnDialogClickListener { position ->
            page = noteContents[position].page
            onChangeContent()
        }
    }

    override fun onPageDown() {
        val total = noteContents.size - 1
        if (isExpand) {
            when (page) {
                total -> {
                    newNoteContent()
                    newNoteContent()
                    page = noteContents.size - 1
                }
                total - 1 -> {
                    newNoteContent()
                    page = noteContents.size - 1
                }
                else -> {
                    page += 2
                }
            }
        } else {
            if (page >= total) {
                newNoteContent()
                page = noteContents.size - 1
            } else {
                page += 1
            }
        }
        onChangeContent()
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                onChangeContent()
            } else if (page == 2) {//当页面不够翻两页时
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
        isExpand = !isExpand
        if (noteContents.size == 1 && isExpand) {
            newNoteContent()
        }
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    override fun onChangeContent() {
        noteContent = noteContents[page]

        if (isExpand) {
            if (page > 0) {
                note_Content_a = noteContents[page - 1]
            } else {
                page = 1
                noteContent = noteContents[page]
                note_Content_a = noteContents[0]
            }
        } else {
            note_Content_a = null
        }

        setElikLoadPath(elik_b!!, noteContent!!)
        tv_page.text = "${page+1}/${noteContents.size}"

        if (isExpand) {
            setElikLoadPath(elik_a!!, note_Content_a!!)
            tv_page.text = "${page}/${noteContents.size}"
            tv_page_a.text="${page+1}/${noteContents.size}"
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, noteContentBean: NoteContent) {
        elik.setLoadFilePath(noteContentBean.filePath, true)
    }

    //创建新的作业内容
    private fun newNoteContent() {

        val date = System.currentTimeMillis()
        val path = FileAddress().getPathNote(typeStr, note?.title, date)
        val pathName = DateUtils.longToString(date)

        noteContent = NoteContent()
        noteContent?.date = date
        noteContent?.typeStr = typeStr
        noteContent?.noteTitle = note?.title
        noteContent?.resId = note?.contentResId
        noteContent?.title = "未命名${noteContents.size + 1}"
        noteContent?.filePath = "$path/$pathName.tch"
        noteContent?.pathName = pathName
        noteContent?.page = noteContents.size
        page = noteContents.size

        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
        val id = NoteContentDaoManager.getInstance().insertId
        noteContent?.id = id

        noteContents.add(noteContent!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        note?.page=page
        NoteDaoManager.getInstance().insertOrReplace(note)
    }

}