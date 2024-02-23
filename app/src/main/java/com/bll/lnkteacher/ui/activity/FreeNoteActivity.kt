package com.bll.lnkteacher.ui.activity

import PopupFreeNoteList
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.manager.FreeNoteDaoManager
import com.bll.lnkteacher.mvp.model.FreeNoteBean
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_free_note.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

class FreeNoteActivity:BaseDrawingActivity() {

    private var bgRes=""
    private var freeNoteBean:FreeNoteBean?=null
    private var posImage=0
    private var images= mutableListOf<String>()//手写地址
    private var bgResList= mutableListOf<String>()//背景地址
    private var freeNotePopWindow:PopupFreeNoteList?=null

    override fun layoutId(): Int {
        return R.layout.ac_free_note
    }
    override fun initData() {
        bgRes= ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
        freeNoteBean= FreeNoteBean()
        freeNoteBean?.date=System.currentTimeMillis()
        freeNoteBean?.title=DateUtils.longToStringNoYear(freeNoteBean?.date!!)
    }

    override fun initView() {
        elik=v_content.pwInterFace
        disMissView(tv_page_title,iv_catalog,iv_btn)
        setPageTitle("随笔")
        tv_name.text=freeNoteBean?.title

        tv_name.setOnClickListener {
            InputContentDialog(this,tv_name.text.toString()).builder().setOnDialogClickListener{
                tv_name.text=it
                freeNoteBean?.title=it
            }
        }

        tv_theme.setOnClickListener {
            NoteModuleAddDialog(this,1).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes=ToolUtils.getImageResStr(this, moduleBean.resFreeNote)
                    v_content.setImageResource(ToolUtils.getImageResId(this,bgRes))
                    bgResList[posImage]=bgRes
                }
        }

        tv_free_list.setOnClickListener {
            if (freeNotePopWindow==null){
                freeNotePopWindow=PopupFreeNoteList(this,tv_free_list).builder()
                freeNotePopWindow?.setOnSelectListener{
                    saveFreeNote()
                    posImage=0
                    freeNoteBean=it
                    bgResList= freeNoteBean?.bgRes as MutableList<String>
                    images= freeNoteBean?.paths as MutableList<String>
                    tv_name.text=freeNoteBean?.title
                    setContentImage()
                }
            }
            else{
                freeNotePopWindow?.show()
            }
        }

        if (posImage>=bgResList.size){
            bgResList.add(bgRes)
        }
        setContentImage()
    }

    override fun onPageDown() {
        posImage+=1
        if (posImage>=bgResList.size){
            bgRes= ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
            bgResList.add(bgRes)
        }
        setContentImage()
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=1
            setContentImage()
        }
    }

    /**
     * 更换内容
     */
    private fun setContentImage(){
        v_content.setImageResource(ToolUtils.getImageResId(this,bgResList[posImage]))
        val path=FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))+"/${posImage+1}.tch"
        //判断路径是否已经创建
        if (!images.contains(path)){
            images.add(path)
        }
        tv_page.text="${posImage+1}/${images.size}"

        elik?.setLoadFilePath(path, true)
    }

    override fun onElikSave() {
        elik?.saveBitmap(true) {}
    }


//    /**
//     * 开始录音
//     */
//    private fun startRecord(){
//        iv_record.setImageResource(R.mipmap.icon_freenote_recording)
//        recordBean = RecordBean()
//        recordBean?.date=System.currentTimeMillis()
//        recordBean?.title=tv_name.text.toString()
//
//        val path= FileAddress().getPathRecord()
//        if (!File(path).exists())
//            File(path).mkdir()
//        recordPath = File(path, "${DateUtils.longToString(recordBean?.date!!)}.mp3").path
//
//        mRecorder = MediaRecorder().apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
//            setOutputFile(recordPath)
//            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
//            try {
//                prepare()//准备
//                start()//开始录音
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    /**
//     * 结束录音
//     */
//    private fun stopRecord(){
//        iv_record.setImageResource(R.mipmap.icon_freenote_recorder)
//        mRecorder?.apply {
//            setOnErrorListener(null)
//            setOnInfoListener(null)
//            setPreviewDisplay(null)
//            stop()
//            release()
//            mRecorder=null
//        }
//        recordBean?.path=recordPath
//        RecordDaoManager.getInstance().insertOrReplace(recordBean)
//        recordBean=null
//        recordPath=null
//    }

    private fun saveFreeNote(){
        //清空没有手写页面
        val sImages= mutableListOf<String>()
        for (i in images.indices){
            if (File(images[i]).exists()){
                sImages.add(images[i])
            }
        }
        freeNoteBean?.paths=images
        freeNoteBean?.bgRes=bgResList
        if (sImages.size>0)
            FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }


    override fun onDestroy() {
        super.onDestroy()
        saveFreeNote()
    }

}
