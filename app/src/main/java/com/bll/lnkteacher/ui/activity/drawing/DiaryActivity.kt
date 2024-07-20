package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CalendarDiaryDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.mvp.model.DiaryBean
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_diary.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
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

        diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong)
        if (diaryBean==null){
            initCurrentDiaryBean()
        }
    }

    override fun initView() {
        disMissView(iv_catalog)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        elik_b?.addOnTopView(ll_date)

        iv_up.setOnClickListener {
            val lastDiaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,0)
            if (lastDiaryBean!=null){
                saveDiary()
                diaryBean=lastDiaryBean
                nowLong=lastDiaryBean.date
                changeContent()
            }
        }

        iv_down.setOnClickListener {
            val nextDiaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,1)
            if (nextDiaryBean!=null){
                saveDiary()
                diaryBean=nextDiaryBean
                nowLong=nextDiaryBean.date
                changeContent()
            }
            else{
                if (nowLong<DateUtils.getStartOfDayInMillis()){
                    nowLong=DateUtils.getStartOfDayInMillis()
                    initCurrentDiaryBean()
                    changeContent()
                }
            }
        }

        tv_date.setOnClickListener {
            CalendarDiaryDialog(this,getCurrentScreenPos()).builder().setOnDateListener{
                saveDiary()
                nowLong=it
                diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong)
                if (diaryBean==null){
                    initCurrentDiaryBean()
                }
                changeContent()
            }
        }

        iv_btn.setOnClickListener {
            NoteModuleAddDialog(this, getCurrentScreenPos(),DataBeanManager.noteModuleDiary).builder()
                .setOnDialogClickListener { moduleBean ->
                    bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    diaryBean?.bgRes=bgRes
                    v_content_b?.setImageResource(ToolUtils.getImageResId(this, bgRes))
                    v_content_a?.setImageResource(ToolUtils.getImageResId(this, bgRes))
                }
        }

        changeContent()
    }

    /**
     * 初始化
     */
    private fun initCurrentDiaryBean(){
        bgRes=ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1)
        diaryBean= DiaryBean()
        diaryBean?.date=nowLong
        diaryBean?.title=DateUtils.longToStringDataNoYear(nowLong)
        diaryBean?.year=DateUtils.getYear()
        diaryBean?.month=DateUtils.getMonth()
        diaryBean?.bgRes=bgRes
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }


    override fun onPageDown() {
        posImage += if (isExpand)2 else 1
        onChangeContent()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= if (isExpand)2 else 1
            onChangeContent()
        }
    }

    /**
     * 切换日记
     */
    private fun changeContent(){
        bgRes=diaryBean?.bgRes.toString()
        images= diaryBean?.paths as MutableList<String>
        posImage=diaryBean?.page!!
        onChangeContent()
    }

    /**
     * 显示内容
     */
    override fun onChangeContent() {
        tv_date.text=DateUtils.longToStringWeek(nowLong)

        if (isExpand){
            if (posImage<1){
                posImage=1
            }
        }
        else{
            if (posImage<0){
                posImage=0
            }
        }

        v_content_b?.setImageResource(ToolUtils.getImageResId(this, bgRes))
        v_content_a?.setImageResource(ToolUtils.getImageResId(this, bgRes))

        val path = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage + 1}.tch"
        setEinkImage(elik_b!!,path)
        tv_page.text = "${posImage + 1}"
        if (isExpand){
            val path = FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${posImage}.tch"
            setEinkImage(elik_a!!,path)
            if (screenPos== Constants.SCREEN_LEFT){
                tv_page_a.text = "${posImage + 1}"
                tv_page.text = "$posImage"
            }
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page.text = "${posImage + 1}"
                tv_page_a.text = "$posImage"
            }
        }

    }

    private fun setEinkImage(eink: EinkPWInterface, path:String){
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        eink.setLoadFilePath(path, true)
        tv_page_total.text="${images.size}"
        tv_page_total_a.text="${images.size}"
    }

    override fun onElikSava_a() {
        elik_a?.saveBitmap(true) {}
    }

    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong))
        if (!File(path).list().isNullOrEmpty()){
            diaryBean?.paths = images
            diaryBean?.page=posImage
            DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDiary()
    }


}