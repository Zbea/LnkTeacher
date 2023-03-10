package com.bll.lnkteacher.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.DrawingCatalogDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.manager.NoteContentDaoManager
import com.bll.lnkteacher.mvp.model.ListItem
import com.bll.lnkteacher.mvp.model.NoteContent
import com.bll.lnkteacher.mvp.model.Notebook
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_note_draw_details.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*

class NoteDrawingActivity : BaseActivity() {

    private var elik:EinkPWInterface?=null

    private var type = 0
    private var noteBook: Notebook? = null
    private var noteContent: NoteContent? = null//当前内容
    private var noteContents = mutableListOf<NoteContent>() //所有内容
    private var page = 0//页码



    override fun layoutId(): Int {
        return R.layout.ac_note_draw_details
    }

    override fun initData() {
        var bundle = intent.getBundleExtra("bundle")
        noteBook = bundle?.getSerializable("note") as Notebook
        type = noteBook?.type!!

        noteContents = NoteContentDaoManager.getInstance().queryAll(type,noteBook?.id!!)

        if (noteContents.size > 0) {
            noteContent = noteContents[noteContents.size - 1]
            page = noteContents.size - 1
        } else {
            newNoteContent()
        }

    }


    override fun initView() {

        v_content.setImageResource(ToolUtils.getImageResId(this,noteBook?.contentResId))//设置背景
        elik = v_content.pwInterFace
        changeContent()

        tv_title.setOnClickListener {
            var title=tv_title.text.toString()
            InputContentDialog(this,title).builder()?.setOnDialogClickListener { string ->
                tv_title.text = string
                noteContent?.title = string
                noteContents[page-1].title = string
                NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
            }
        }


        btn_page_down.setOnClickListener {
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

        btn_page_up.setOnClickListener {
            if (page>0){
                page-=1
                changeContent()
            }
        }

        iv_catalog.setOnClickListener {
            showCatalog()
        }


        iv_btn.setOnClickListener {

        }

    }



    /**
     * 弹出目录
     */
    private fun showCatalog(){
        var titleStr=""
        var list= mutableListOf<ListItem>()
        for (item in noteContents){
            val listItem= ListItem()
            listItem.name=item.title
            listItem.page=item.page
            if (titleStr != item.title)
            {
                titleStr=item.title
                list.add(listItem)
            }

        }
        DrawingCatalogDialog(this,list).builder()?.
        setOnDialogClickListener { position ->
            page = noteContents[position].page
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent() {

        noteContent = noteContents[page]
        tv_title.text=noteContent?.title
        tv_page.text = (page + 1).toString()

        elik?.setPWEnabled(true)
        elik?.setLoadFilePath(noteContent?.filePath!!, true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }

        })
    }


    //创建新的作业内容
    private fun newNoteContent() {

        val path=FileAddress().getPathNote(type,noteBook?.id,noteContents.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        noteContent = NoteContent()
        noteContent?.date = System.currentTimeMillis()
        noteContent?.type=type
        noteContent?.notebookId = noteBook?.id
        noteContent?.resId = noteBook?.contentResId

        noteContent?.title="未命名${noteContents.size+1}"
        noteContent?.folderPath=path
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