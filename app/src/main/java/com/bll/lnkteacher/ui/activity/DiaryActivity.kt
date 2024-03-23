package com.bll.lnkteacher.ui.activity

import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.DateCalendarDialog
import com.bll.lnkteacher.dialog.DiaryListDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.mvp.model.DiaryBean
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_diary.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class DiaryActivity:BaseDrawingActivity() {

    private var nowLong=0L//当前时间
    private var diaryBean:DiaryBean?=null
    private var images = mutableListOf<String>()//手写地址
    private var posImage=0
    private var bgRes=""

    override fun layoutId(): Int {
        return R.layout.ac_diary
    }

    override fun initData() {
        nowLong=DateUtils.getStartOfDayInMillis()
    }

    override fun initView() {
        disMissView(tv_page_title)
        elik_b?.addOnTopView(ll_date)
        elik_b?.addOnTopView(tv_digest)

        onChangeContent()

        iv_up.setOnClickListener {
            saveDiary()
            nowLong -= Constants.dayLong
            onChangeContent()
        }

        iv_down.setOnClickListener {
            saveDiary()
            nowLong += Constants.dayLong
            onChangeContent()
        }

        tv_date.setOnClickListener {
            DateCalendarDialog(this,50f,150f).builder().setOnDateListener{
                saveDiary()
                nowLong=it
                onChangeContent()
            }
        }

        tv_digest.setOnClickListener {
            InputContentDialog(this,diaryBean?.title!!).builder().setOnDialogClickListener{
                diaryBean?.title=it
            }
        }

        iv_btn.setOnClickListener {
            NoteModuleAddDialog(this, getCurrentScreenPos(),0).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    diaryBean?.bgRes=bgRes
                    v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
                }
        }
    }

    override fun onCatalog() {
        DiaryListDialog(this,nowLong).builder()?.setOnDialogClickListener(object : DiaryListDialog.OnDialogClickListener {
            override fun onClick(diaryBean: DiaryBean) {
                saveDiary()
                nowLong=diaryBean.date
                onChangeContent()
            }
            override fun onDelete(diaryBean: DiaryBean) {
                for (i in 0.until(diaryBean.size)){
                    val path=FileAddress().getPathDiary(DateUtils.longToString(diaryBean.date)) + "/${i + 1}.tch"
                    elik_b?.freeCachePWBitmapFilePath(path, true)
                }
                FileUtils.deleteFile(File(FileAddress().getPathDiary(DateUtils.longToString(diaryBean.date))))
                DiaryDaoManager.getInstance().delete(diaryBean)
            }
        })
    }

    override fun onPageDown() {
        posImage += 1
        setContentImage()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= 1
            setContentImage()
        }
    }

    override fun onChangeContent() {
        images.clear()
        posImage=0
        diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong)
        if (diaryBean!=null){
            bgRes=diaryBean?.bgRes.toString()
            for (i in 0.until(diaryBean?.size!!)){
                val path=FileAddress().getPathDiary(DateUtils.longToString(nowLong)) + "/${i + 1}.tch"
                images.add(path)
            }
        } else{
            bgRes=ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1)
            diaryBean= DiaryBean()
            diaryBean?.date=nowLong
            diaryBean?.title=DateUtils.longToStringDataNoYear(nowLong)
            diaryBean?.bgRes=bgRes
        }
        setContentImage()
    }

    /**
     * 显示内容
     */
    private fun setContentImage() {
        tv_date.text=DateUtils.longToStringWeek(nowLong)
        v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
        val path = FileAddress().getPathDiary(DateUtils.longToString(nowLong)) + "/${posImage + 1}.tch"
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        tv_page.text = "${posImage + 1}/${images.size}"

        elik_b?.setLoadFilePath(path, true)
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToString(nowLong))
        if (!File(path).list().isNullOrEmpty()){
            diaryBean?.size = images.size
            DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDiary()
    }

}