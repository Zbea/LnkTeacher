package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.EinkPWInterface
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogDialog
import com.bll.lnkteacher.dialog.HomeworkPublishDialog
import com.bll.lnkteacher.manager.HomeworkContentDaoManager
import com.bll.lnkteacher.manager.HomeworkContentTypeDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentBean
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentTypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total

class HomeworkContentDrawingActivity : BaseDrawingActivity(),IContractView.IQiniuView,IContractView.IHomeworkAssignView {

    private val mUploadPresenter = QiniuPresenter(this, 3)
    private val mPresenter= HomeworkAssignPresenter(this)
    private var contentId = 0
    private var typeBean: TypeBean?=null//作业卷分类
    private var contentTypeBean: HomeworkContentTypeBean? = null
    private var content_b: HomeworkContentBean? = null//当前内容
    private var content_a: HomeworkContentBean? = null//a屏内容
    private var contentBeans = mutableListOf<HomeworkContentBean>() //所有内容
    private var page = 0//页码
    private var currentAssignItem:HomeworkAssignItem?=null

    override fun onToken(token: String) {
        val paths= mutableListOf<String>()
        for (item in contentBeans){
            paths.add(item.mergePath)
        }
        FileImageUploadManager(token, paths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map=HashMap<String,Any>()
                    map["title"]=currentAssignItem!!.contentStr
                    map["classIds"]=currentAssignItem!!.classIds
                    map["showStatus"]=currentAssignItem!!.showStatus
                    map["endTime"]=if (currentAssignItem!!.showStatus==0) currentAssignItem?.endTime!!/1000 else 0
                    map["commonTypeId"]=typeBean!!.id
                    map["examUrl"]=ToolUtils.getImagesStr(urls)
                    mPresenter.commitHomework(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onCommitSuccess() {
        showToast("作业布置成功")
        contentTypeBean?.title=currentAssignItem?.contentStr
        contentTypeBean?.isSave=true
        HomeworkContentTypeDaoManager.getInstance().insertOrReplace(contentTypeBean)
        //删除合图
        FileUtils.delete(contentTypeBean?.path+"/merge/")
        setResult(Constants.RESULT_10001, Intent().putExtra("contentTitle",currentAssignItem?.contentStr))
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("homeworkType") as TypeBean
        val id = intent.getIntExtra("contentId",0)
        contentTypeBean= HomeworkContentTypeDaoManager.getInstance().queryBean(id)
        contentId=contentTypeBean?.contentId!!
        contentBeans = HomeworkContentDaoManager.getInstance().queryAllByContent(contentId)

        if (contentBeans.isNotEmpty()) {
            content_b = contentBeans[contentBeans.size - 1]
            page = contentBeans.size - 1
        } else {
            newCreateContent()
        }
    }

    override fun initView() {
        MethodManager.setImageResource(this,R.mipmap.icon_note_content_hg_9,v_content_a)
        MethodManager.setImageResource(this,R.mipmap.icon_note_content_hg_9,v_content_b)

        if (contentTypeBean?.isSave==true)
            disMissView(iv_btn)

        iv_btn.setImageResource(R.mipmap.icon_draw_commit)
        iv_btn.setOnClickListener {
            if (FileUtils.isExistContent(contentTypeBean?.path)){
                //未手写部分保存本地图片
                for (item in contentBeans){
                    if (!FileUtils.isExist(item.mergePath)){
                        val options = BitmapFactory.Options()
                        options.inScaled = false // 防止自动缩放
                        val bitmap=BitmapFactory.decodeResource(resources,R.mipmap.icon_note_content_hg_9,options)
                        BitmapUtils.saveBmpGallery(bitmap,item.mergePath)
                    }
                }
                HomeworkPublishDialog(this,typeBean!!).builder().setOnDialogClickListener{
                    currentAssignItem=it
                    mUploadPresenter.getToken(true)
                }
            }
            else{
                showToast(2,"暂无内容，无法布置")
            }
        }

        onChangeContent()
    }


    override fun onCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in contentBeans) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = contentBeans.indexOf(item)
            itemList.isEdit=false
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }
        }
        CatalogDialog(this, screenPos,getCurrentScreenPos(),list,true).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(pageNumber: Int) {
                if (page!=pageNumber){
                    page = pageNumber
                    onChangeContent()
                }
            }
        })
    }

    override fun onPageDown() {
        val total = contentBeans.size - 1
        if(isExpand){
            if (page<total-1){
                page+=2
                onChangeContent()
            }
            else{
                newCreateContent()
                page=contentBeans.size-1
                onChangeContent()
            }
        }
        else{
            if (page ==total) {
                newCreateContent()
                page=contentBeans.size-1
                onChangeContent()
            } else {
                page += 1
                onChangeContent()
            }
        }
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                onChangeContent()
            } else if (page == 2) {
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
        if (contentBeans.size==1){
            newCreateContent()
        }
        if (page==0){
            page=1
        }
        isExpand = !isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    override fun onChangeContent() {
        content_b = contentBeans[page]
        if (isExpand) {
            content_a = contentBeans[page-1]
        }

        tv_page_total.text="${contentBeans.size}"
        tv_page_total_a.text="${contentBeans.size}"

        setElikLoadPath(elik_b!!, content_b!!.filePath)
        tv_page.text = "${page+1}"
        if (isExpand) {
            setElikLoadPath(elik_a!!, content_a!!.filePath)
            if (screenPos==Constants.SCREEN_RIGHT){
                tv_page_a.text="$page"
            }
            else{
                tv_page.text="$page"
                tv_page_a.text="${page+1}"
            }
        }
    }

    //保存绘图以及更新手绘
    private fun setElikLoadPath(elik: EinkPWInterface, path: String) {
        elik.setLoadFilePath(path, true)
    }

    override fun onElikSava_a() {
        if (isExpand&&contentTypeBean?.isSave==false)
            bitmapBatchSaver.submitBitmap(BitmapUtils.loadBitmapFromViewByCanvas(v_content_a),content_a?.mergePath,null)
    }

    override fun onElikSava_b() {
        if (contentTypeBean?.isSave==false)
            bitmapBatchSaver.submitBitmap(BitmapUtils.loadBitmapFromViewByCanvas(v_content_b),content_b?.mergePath,null)
    }

    //创建新的作业内容
    private fun newCreateContent() {
        val date = System.currentTimeMillis()

        content_b = HomeworkContentBean()
        content_b?.date = date
        content_b?.typeId = contentTypeBean?.typeId
        content_b?.contentId=contentId
        content_b?.title = "未命名${contentBeans.size + 1}"
        content_b?.filePath =getPathHomeworkContent(date)
        content_b?.mergePath=getPathHomeworkContentMerge(date)
        page = contentBeans.size

        HomeworkContentDaoManager.getInstance().insertOrReplaceNote(content_b)
        val id = HomeworkContentDaoManager.getInstance().insertId
        content_b?.id = id

        contentBeans.add(content_b!!)
    }

    /**
     * 老师手写地址
     */
    private fun getPathHomeworkContent(date:Long):String{
        return "${contentTypeBean?.path}/${DateUtils.longToString(date)}.png"
    }
    /**
     * 老师手写地址
     */
    private fun getPathHomeworkContentMerge(date:Long):String{
        return "${contentTypeBean?.path}/merge/${DateUtils.longToString(date)}.png"
    }

}