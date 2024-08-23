package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.DrawingCatalogDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.NoteContent
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*

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

        if (noteContents.size > 0) {
            note_Content_b = noteContents[noteContents.size - 1]
            page = note?.page!!
        } else {
            newNoteContent()
        }
    }


    override fun initView() {
        disMissView(iv_btn)
        v_content_a?.setImageResource(ToolUtils.getImageResId(this, note?.contentResId))//设置背景
        v_content_b?.setImageResource(ToolUtils.getImageResId(this, note?.contentResId))//设置背景
        onChangeContent()
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
        DrawingCatalogDialog(this, screenPos,getCurrentScreenPos(),list).builder().setOnDialogClickListener(object : DrawingCatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=noteContents[position].page){
                    page = noteContents[position].page
                    onChangeContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
                val item=noteContents[position]
                item.title=title
                NoteContentDaoManager.getInstance().insertOrReplaceNote(item)
            }
        })
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
        note_Content_b = noteContents[page]
        if (isExpand) {
            if (page<=0){
                page=1
                note_Content_b = noteContents[page]
            }
            note_Content_a = noteContents[page-1]
        }

        tv_page_total.text="${noteContents.size}"
        tv_page_total_a.text="${noteContents.size}"

        setElikLoadPath(elik_b!!, note_Content_b!!.filePath)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, note_Content_a!!.filePath)
            if (screenPos== Constants.SCREEN_LEFT){
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
                tv_page.text="${page+1}"
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
        val path = FileAddress().getPathNote(typeStr, note?.title, date)
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