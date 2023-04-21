package com.bll.lnkteacher.ui.activity.teaching

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.EinkPWInterface
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrectClass
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperGrade
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import java.io.File

class TestPaperCorrectActivity:BaseActivity(),IContractView.ITestPaperCorrectDetailsView,IFileUploadView{

    private var id=0
    private val mUploadPresenter=FileUploadPresenter(this)
    private val mPresenter=TestPaperCorrectDetailsPresenter(this)
    private var mClassBean: CorrectClassBean?=null
    private var userItems= mutableListOf<TestPaperCorrectClass.UserBean>()

    private var mAdapter:TestPaperCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages:Array<String>?=null

    private var elik: EinkPWInterface? = null

    override fun onSuccess(urls: MutableList<String>?) {
        url=ToolUtils.getImagesStr(urls)
        val map= HashMap<String, Any>()
        map["studentTaskId"]=userItems[posUser].studentTaskId
        map["score"]=getScoreInput()
        map["submitUrl"]=url
        map["status"]=2
        mPresenter.commitPaperStudent(map)
    }


    override fun onImageList(list: MutableList<ContentListBean>?) {
    }
    override fun onClassPapers(bean: TestPaperCorrectClass?) {
        val beans=bean?.list!!
        for (item in beans){
            if (item.status!=3){
                userItems.add(item)
            }
        }
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        else{
            setEditState(false)
        }
        mAdapter?.setNewData(userItems)
    }
    override fun onGrade(list: MutableList<TestPaperGrade>?) {
    }
    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name+getString(R.string.teaching_correct_success))
        userItems[posUser].score=getScoreInput()
        userItems[posUser].submitUrl=url
        userItems[posUser].status=2
        mAdapter?.notifyDataSetChanged()
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik?.setPWEnabled(false)
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        id=intent.flags
        mClassBean=intent.getBundleExtra("bundle").getSerializable("classBean") as CorrectClassBean

        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun initView() {
        elik=iv_image.pwInterFace
        elik?.setPWEnabled(false)

        setPageTitle("${getString(R.string.teaching_tab_testpaper_correct)}  ${mClassBean?.examName}  ${mClassBean?.name}")

        initRecyclerView()

        et_score.doAfterTextChanged {
            if (getScoreInput()>0&& userItems[posUser].status==1){
                showLoading()
                commitPapers()
            }
        }

        iv_up.setOnClickListener {
            if (posImage>0){
                posImage-=1
                setContentImage()
            }
        }
        iv_down.setOnClickListener {
            if (posImage< getImageSize()-1){
                posImage+=1
                setContentImage()
            }
        }
    }

    private fun initRecyclerView(){

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(this@TestPaperCorrectActivity,20f)))
            setOnItemClickListener { adapter, view, position ->
                userItems[posUser].isCheck=false
                posUser=position//设置当前学生下标
                userItems[posUser].isCheck=true
                notifyDataSetChanged()
                posImage=0
                et_score.setText("")
                setEditState(true)
                setContentView()
            }
        }

    }

    /**
     * 提交学生考卷
     */
    private fun commitPapers(){
        val paths= mutableListOf<String>()
        //手写,图片合图
        for (i in currentImages?.indices!!){
            val index=i+1
            val masterImage="${getPath()}/${index}.png"//原图
            val drawPath = getPathDrawStr(index).replace("tch","png")
            val mergePath = getPath()//合并后的路径
            val mergePathStr = "${getPath()}/merge${index}.png"//合并后图片地址

            val oldBitmap = BitmapFactory.decodeFile(masterImage)
            val drawBitmap = BitmapFactory.decodeFile(drawPath)
            if (drawBitmap != null) {
                val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge${index}")
            } else {
                BitmapUtils.saveBmpGallery(this, oldBitmap, mergePath, "merge${index}")
            }
            paths.add(mergePathStr)
        }
        Handler().postDelayed({
            mUploadPresenter.upload(paths)
        },500)
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        val userItem=userItems[posUser]
        if (userItem.status==2){
            currentImages=userItem.submitUrl.split(",").toTypedArray()
            et_score.setText(userItem.score.toString())
            setEditState(false)
            elik?.setPWEnabled(false)
            setContentImage()
        }
        else{
            currentImages=userItem.studentUrl.split(",").toTypedArray()
            elik?.setPWEnabled(true)
            loadPapers()
        }

    }

    /**
     * 设置输入分数是否可以编辑
     */
    private fun setEditState(boolean: Boolean){
        et_score.isFocusable = boolean
        et_score.isFocusableInTouchMode = boolean
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
        //批改成功后删掉原来，加载提交后的图片
        if (userItems[posUser].status==2){
            GlideUtils.setImageRoundUrl(this, currentImages?.get(posImage) ,iv_image,10)
        }
        else{
            val masterImage="${getPath()}/${posImage+1}.png"//原图
            GlideUtils.setImageFile(this,File(masterImage),iv_image)
        }
        tv_page.text="${posImage+1}/${getImageSize()}"

        val drawPath = getPathDrawStr(posImage+1)
        elik?.setLoadFilePath(drawPath, true)
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

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers(){
        showLoading()
        val file = File(getPath())
        val files = FileUtils.getFiles(file.path)
        if (files.isNullOrEmpty()) {
            val imageDownLoad = ImageDownLoadUtils(this, currentImages, file.path)
            imageDownLoad.startDownload()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    hideLoading()
                    runOnUiThread {
                        setContentImage()
                    }
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    imageDownLoad.reloadImage()
                }
            })
        }
        else{
            setContentImage()
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (currentImages.isNullOrEmpty())
            return 0
        return currentImages!!.size
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathTestPaper(id,mClassBean?.classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

    /**
     * 得到当前输入分数
     */
    private fun getScoreInput():Int{
        var score=0
        val scoreStr=et_score.text.toString()
        if (scoreStr.isNotEmpty())
            score=scoreStr.toInt()
        return  score
    }

}