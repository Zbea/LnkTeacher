package com.bll.lnkteacher.ui.activity.book

import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUtils
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class TextBookAnnotationActivity:BaseDrawingActivity() {
    private var path=""
    private var paths= mutableListOf<String>()
    private var posImage = 0

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        path= intent.getStringExtra("path").toString()

        setData()
    }

    override fun initView() {
        MethodManager.setImageResource(this, R.mipmap.icon_note_content_hg_11,v_content_a)
        MethodManager.setImageResource(this, R.mipmap.icon_note_content_hg_11,v_content_b)

        disMissView(iv_expand,iv_btn,iv_tool,iv_catalog)

        onChangeContent()
    }

    override fun onPageDown() {
        if (posImage==paths.size-1){
            if (isDrawLastContent()){
                paths.add(getPathStr())
                posImage+=1
                onChangeContent()
            }
        }
        else{
            posImage+=1
            onChangeContent()
        }
    }

    override fun onPageUp() {
        if (posImage>0)
            posImage-=1
        onChangeContent()
    }

    override fun onChangeContent() {
        tv_page.text = "${posImage + 1}"
        tv_page_total.text="${paths.size}"
        elik_b?.setLoadFilePath(paths[posImage], true)
    }

    private fun setData(){
        paths.clear()
        val files=FileUtils.getFiles(path)
        for (file in files){
            paths.add(file.path)
        }
        if (paths.isEmpty()){
            paths.add(getPathStr())
        }
    }

    /**
     * 最后一个是否已写
     */
    private fun isDrawLastContent():Boolean{
        return File(paths.last()).exists()
    }

    private fun getPathStr():String{
        return path+"/${DateUtils.longToString(System.currentTimeMillis())}.png"
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.TEXTBOOK_ANNOTATION_CHANGE_PAGE_EVENT){
            posImage=0
            path=DataBeanManager.textBookAnnotationPath
            setData()
            onChangeContent()
        }
    }

}