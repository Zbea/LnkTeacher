package com.bll.lnkteacher.ui.activity

import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.DrawingCatalogDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.NoteContent
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseDrawingActivity() {

    private var typeStr = ""
    private var note: Note? = null
    private var noteContent: NoteContent? = null//当前内容
    private var noteContents = mutableListOf<NoteContent>() //所有内容
    private var page = 0//页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("bundle")
        note = bundle?.getSerializable("noteBundle") as Note
        typeStr = note?.typeStr.toString()

        noteContents = NoteContentDaoManager.getInstance().queryAll(typeStr,note?.title)

        if (noteContents.size > 0) {
            noteContent = noteContents[noteContents.size - 1]
            page = noteContents.size - 1
        } else {
            newNoteContent()
        }

    }


    override fun initView() {
        disMissView(iv_btn)
        v_content.setImageResource(ToolUtils.getImageResId(this,note?.contentResId))//设置背景
        changeContent()
    }

    override fun setDrawingTitle(title: String) {
        noteContent?.title = title
        noteContents[page-1].title = title
        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
    }

    override fun onCatalog() {
        var titleStr=""
        val list= mutableListOf<ItemList>()
        for (item in noteContents){
            val itemList= ItemList()
            itemList.name=item.title
            itemList.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(itemList)
            }

        }
        DrawingCatalogDialog(this,list).builder().
        setOnDialogClickListener { position ->
            page = noteContents[position].page
            changeContent()
        }
    }

    override fun onPageDown() {
        val total=noteContents.size-1
        when(page){
            total->{
                newNoteContent()
            }
            else->{
                page += 1
            }
        }
        changeContent()
    }

    override fun onPageUp() {
        if (page>0){
            page-=1
            changeContent()
        }
    }


    //翻页内容更新切换
    private fun changeContent() {
        noteContent = noteContents[page]
        tv_page_title.text=noteContent?.title
        tv_page.text = (page + 1).toString()

        elik?.setPWEnabled(true)
        elik?.setLoadFilePath(noteContent?.filePath!!, true)
    }

    override fun onElikSave() {
        elik?.saveBitmap(true) {}
    }


    //创建新的作业内容
    private fun newNoteContent() {

        val date=System.currentTimeMillis()
        val path=FileAddress().getPathNote(typeStr,note?.title,date)
        val pathName = DateUtils.longToString(date)

        noteContent = NoteContent()
        noteContent?.date = date
        noteContent?.typeStr=typeStr
        noteContent?.noteTitle = note?.title
        noteContent?.resId = note?.contentResId
        noteContent?.title="未命名${noteContents.size+1}"
        noteContent?.filePath = "$path/$pathName.tch"
        noteContent?.pathName=pathName
        noteContent?.page = noteContents.size
        page = noteContents.size

        NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
        val id= NoteContentDaoManager.getInstance().insertId
        noteContent?.id=id

        noteContents.add(noteContent!!)
    }

}