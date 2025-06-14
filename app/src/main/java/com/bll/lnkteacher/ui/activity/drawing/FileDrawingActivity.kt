package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFileDrawingActivity
import com.bll.lnkteacher.dialog.CatalogDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.utils.FileUtils
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File


class FileDrawingActivity : BaseFileDrawingActivity() {

    private var path=""

    override fun layoutId(): Int {
        return R.layout.ac_drawing_file
    }

    override fun initData() {
        pageIndex = intent.getIntExtra("pageIndex", 0)
        path= intent.getStringExtra("pagePath").toString()
        pageCount=FileUtils.getAscFiles(path).size
    }

    override fun initView() {
        disMissView(iv_btn)

        onChangeContent()
    }

    override fun onCatalog() {
        val files = FileUtils.getAscFiles(path)
        val list= mutableListOf<ItemList>()
        for (file in files){
            val itemList= ItemList()
            itemList.name=file.name.replace(".png","")
            itemList.page=files.indexOf(file)
            list.add(itemList)
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,false).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (pageIndex!=pageNumber){
                    pageIndex = pageNumber
                    onChangeContent()
                }
            }
        })
    }

    override fun onPageUp() {
        if (pageIndex > 0) {
            pageIndex -= if(isExpand)2 else 1
            onChangeContent()
        }
    }

    override fun onPageDown() {
        if (pageIndex<pageCount-1){
            pageIndex+=if(isExpand)2 else 1
            onChangeContent()
        }
    }

    override fun onChangeExpandContent() {
        if (pageCount>1){
            changeErasure()
            isExpand=!isExpand
            moveToScreen(isExpand)
            onChangeExpandView()
            onChangeContent()
        }
    }

    /**
     * 更新内容
     */
    override fun onChangeContent() {
        if (pageCount==0)
            return
        if (pageIndex<0)
            pageIndex=0
        if (pageIndex>=pageCount){
            pageIndex=pageCount-1
        }
        if (pageIndex>pageCount-2&&isExpand)
            pageIndex=pageCount-2

        tv_page_total.text="$pageCount"
        tv_page_total_a.text="$pageCount"

        if (isExpand){
            val page_up=pageIndex+1//上一页页码
            loadPicture(pageIndex, elik_a!!, v_content_a!!)
            loadPicture(page_up, elik_b!!, v_content_b!!)

            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text = "${pageIndex+1}"
                tv_page.text="${page_up+1}"
            }
            else{
                tv_page.text = "${pageIndex+1}"
                tv_page_a.text="${page_up+1}"
            }
        }
        else{
            tv_page.text = "${pageIndex+1}"
            loadPicture(pageIndex, elik_b!!, v_content_b!!)
        }
    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val files = FileUtils.getAscFiles(path)
        if (index<files.size){
            val showFile=files[index]
            MethodManager.setImageFile(showFile.absolutePath,view)
            elik.setLoadFilePath(getDrawingPath(showFile), true)
        }
    }

    /**
     * 得到提错本的手写路径
     */
    private fun getDrawingPath(file: File):String{
        return "$path/drawing/${file.name}"
    }
}