package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CalendarDiaryDialog
import com.bll.lnkteacher.dialog.CatalogDiaryDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ModuleItemDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.mvp.model.DiaryBean
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing.ll_diary
import kotlinx.android.synthetic.main.ac_drawing.tv_digest
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class DiaryActivity:BaseDrawingActivity() {

    private var uploadId=0
    private var nowLong=0L//当前时间
    private var diaryBean:DiaryBean?=null
    private var images = mutableListOf<String>()//手写地址
    private var posImage=0
    private var bgRes=""

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        uploadId=intent.flags
        nowLong=DateUtils.getStartOfDayInMillis()

        if (uploadId==0){
            diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,uploadId)
            if (diaryBean==null){
                initCurrentDiaryBean()
            }
            changeContent()
        }
        else{
            diaryBean=DiaryDaoManager.getInstance().queryBean(uploadId)
            if (diaryBean!=null){
                changeContent()
            }
        }
    }

    override fun initView() {
        iv_btn.setImageResource(R.mipmap.icon_draw_change)
        showView(ll_diary)
        elik_b?.addOnTopView(ll_diary)

        iv_up.setOnClickListener {
            val lastDiaryBean=DiaryDaoManager.getInstance().queryBeanByDate(nowLong,0,uploadId)
            if (lastDiaryBean!=null){
                saveDiary()
                diaryBean=lastDiaryBean
                changeContent()
            }
        }

        iv_down.setOnClickListener {
            val nextDiaryBean=DiaryDaoManager.getInstance().queryBeanByDate(nowLong,1,uploadId)
            if (nextDiaryBean!=null){
                saveDiary()
                diaryBean=nextDiaryBean
                changeContent()
            }
            else{
                //本地日记：当最新的当天还没有保存时，可以切换到当天
                if (uploadId==0){
                    if (nowLong<DateUtils.getStartOfDayInMillis()){
                        saveDiary()
                        nowLong=DateUtils.getStartOfDayInMillis()
                        initCurrentDiaryBean()
                        changeContent()
                    }
                }
            }
        }

        tv_date.setOnClickListener {
            CalendarDiaryDialog(this,getCurrentScreenPos(),uploadId).builder().setOnDateListener{
                if (nowLong!=it){
                    saveDiary()
                    nowLong=it
                    diaryBean=DiaryDaoManager.getInstance().queryBean(nowLong,uploadId)
                    if (nowLong==DateUtils.getStartOfDayInMillis()&&diaryBean == null) {
                        initCurrentDiaryBean()
                    }
                    changeContent()
                }
            }
        }

        iv_btn.setOnClickListener {
            ModuleItemDialog(this, getCurrentScreenPos(),"日记模板",DataBeanManager.diaryModules).builder()
                .setOnDialogClickListener { moduleBean ->
                    bgRes= ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    diaryBean?.bgRes=bgRes
                    setBg()
                    SPUtil.putString(Constants.SP_DIARY_BG_SET,bgRes)
                }
        }

        tv_digest.setOnClickListener {
            InputContentDialog(this,getCurrentScreenPos(),if (diaryBean?.title.isNullOrEmpty()) "输入摘要" else diaryBean?.title!!).builder().setOnDialogClickListener{
                diaryBean?.title=it
                saveDiary()
            }
        }

    }

    /**
     * 初始化
     */
    private fun initCurrentDiaryBean(){
        posImage=0
        bgRes= SPUtil.getString(Constants.SP_DIARY_BG_SET).ifEmpty { ToolUtils.getImageResStr(this,R.mipmap.icon_diary_details_bg_1) }
        diaryBean= DiaryBean()
        diaryBean?.date=nowLong
        diaryBean?.year=DateUtils.getYear()
        diaryBean?.month=DateUtils.getMonth()
        diaryBean?.bgRes=bgRes
        diaryBean?.paths= mutableListOf(getPath(posImage))
    }

    /**
     * 切换日记
     */
    private fun changeContent(){
        nowLong = diaryBean?.date!!
        if (nowLong==DateUtils.getStartOfDayInMillis()&&uploadId==0){
            showView(iv_btn)
        }
        else {
            disMissView(iv_btn)
        }
        bgRes=diaryBean?.bgRes.toString()
        images= diaryBean?.paths as MutableList<String>
        posImage=diaryBean?.page!!
        tv_date.text=DateUtils.longToStringWeek(nowLong)
        setBg()
        setDisableTouchInput(diaryBean?.isUpload!!)
        onChangeContent()
    }

    override fun onCatalog() {
        val diaryBeans=DiaryDaoManager.getInstance().queryListByTitle(uploadId)
        CatalogDiaryDialog(this,screenPos,getCurrentScreenPos(),diaryBeans).builder().setOnDialogClickListener { position ->
            if (nowLong != diaryBeans[position]?.date) {
                saveDiary()
                diaryBean = diaryBeans[position]
                changeContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        //云书库下载日记，如果只存在一页不能全屏
        if (uploadId>0&&images.size==1){
            return
        }
        //本地日记:如果在一页时全屏，已写则默认创建新页，否则无法全屏
        if (images.size==1){
            if (File(images[posImage]).exists()){
                images.add(getPath(posImage+1))
            }
            else{
                return
            }
        }

        changeErasure()
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageDown() {
        val total=images.size-1
        if (posImage>total){
            posImage=total
            onChangeContent()
        }
        else{
            if(isExpand){
                if (posImage<total-1){
                    posImage+=2
                    onChangeContent()
                }
                else{
                    if (isDrawLastContent()){
                        images.add(getPath(images.size))
                    }
                    posImage=images.size-1
                    onChangeContent()
                }
            }
            else{
                if (posImage<total){
                    posImage += 1
                    onChangeContent()
                }
                else{
                    if (isDrawLastContent()){
                        images.add(getPath(images.size))
                        posImage+=1
                        onChangeContent()
                    }
                }
            }
        }
    }

    override fun onPageUp() {
        if(isExpand){
            if (posImage>2){
                posImage-=2
                onChangeContent()
            }
            else if (posImage==2){
                posImage=1
                onChangeContent()
            }
        }else{
            if (posImage>0){
                posImage-=1
                onChangeContent()
            }
        }
    }



    /**
     * 显示内容
     */
    override fun onChangeContent() {
        if (isExpand&&posImage==0){
            posImage=1
        }

        val path = getPath(posImage)

        setEinkImage(elik_b!!,path)
        tv_page.text = "${posImage + 1}"

        if (isExpand){
            val path_a =getPath(posImage-1)
            setEinkImage(elik_a!!,path_a)
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text = "$posImage"
            }
            else{
                tv_page_a.text = "${posImage + 1}"
                tv_page.text = "$posImage"
            }
        }

        tv_page_total.text="${images.size}"
        tv_page_total_a.text="${images.size}"
    }

    private fun setEinkImage(eink:EinkPWInterface,path:String){
        eink.setLoadFilePath(path, true)
    }

    private fun setBg(){
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this, bgRes),v_content_a)
        MethodManager.setImageResource(this,ToolUtils.getImageResId(this, bgRes),v_content_b)
    }

    /**
     * 当前本地日记并且最后一个已写
     */
    private fun isDrawLastContent():Boolean{
        val path = images.last()
        return File(path).exists()&&uploadId==0
    }

    private fun getPath(index:Int):String{
        return FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong)) + "/${index + 1}.png"
    }

    private fun saveDiary() {
        val path=FileAddress().getPathDiary(DateUtils.longToStringCalender(nowLong))
        if (FileUtils.isExistContent(path)||!diaryBean?.title.isNullOrEmpty()){
            diaryBean?.paths = images
            diaryBean?.page=posImage
            DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
        }
    }

    override fun onPause() {
        super.onPause()
        saveDiary()
    }
}