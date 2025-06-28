package com.bll.lnkteacher.ui.activity

import android.widget.ImageView
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.utils.FileMultitaskDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_lessons_full_image.btn_page_down
import kotlinx.android.synthetic.main.ac_lessons_full_image.btn_page_up
import kotlinx.android.synthetic.main.ac_lessons_full_image.iv_image
import kotlinx.android.synthetic.main.ac_lessons_full_image.iv_move_down
import kotlinx.android.synthetic.main.ac_lessons_full_image.iv_move_up
import kotlinx.android.synthetic.main.ac_lessons_full_image.sv_view
import kotlinx.android.synthetic.main.ac_lessons_full_image.tv_page_current
import kotlinx.android.synthetic.main.ac_lessons_full_image.tv_page_total_bottom


class LessonsFullImageActivity:BaseDrawingActivity() {
    private var path=""
    private var moveY=0
    private var images= mutableListOf<String>()
    private var paths= mutableListOf<String>()
    private var posImage=0

    override fun layoutId(): Int {
        return R.layout.ac_lessons_full_image
    }

    override fun initData() {
        path= intent.getStringExtra("imagePath").toString()
        val url=intent.getStringExtra("imageUrl")
        images= url?.split(",") as MutableList<String>
        for (i in images.indices){
            paths.add(path + "/full/${i + 1}.png")
        }

        if (FileUtils.isExistContent(path)){
            onChangeContent()
        }
        else{
            downloadImage()
        }
    }

    override fun initView() {
        iv_move_up.setOnClickListener {
            if (moveY>0)
                moveY-=600
            sv_view.scrollTo(0, moveY)
        }
        iv_move_down.setOnClickListener {
            moveY+=600
            sv_view.scrollTo(0, moveY)
        }

        btn_page_up.setOnClickListener {
            moveY=0
            sv_view.scrollTo(0,0)
            onPageUp()
        }

        btn_page_down.setOnClickListener {
            moveY=0
            sv_view.scrollTo(0,0)
            onPageDown()
        }
    }

    override fun onPageUp() {
        if (posImage>0)
            posImage-=1
        onChangeContent()
    }

    override fun onPageDown() {
        if (posImage<images.size-1)
            posImage+=1
        onChangeContent()
    }


    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent() {
        tv_page_current.text="${posImage+1}"
        tv_page_total_bottom.text = "${images.size}"
        MethodManager.setImageFile(paths[posImage],iv_image)
//        GlideUtils.setImageNoCacheUrl(this, images[posImage], iv_image)
        iv_image.scaleType= ImageView.ScaleType.FIT_CENTER
    }

    private fun downloadImage(){
        FileMultitaskDownManager.with(this).create(images).setPath(paths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    onChangeContent()
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.delete(path)
    }
}