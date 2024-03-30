package com.bll.lnkteacher.ui.activity.exam

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.mvp.presenter.ExamCorrectPresenter
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.ExamCorrectUserAdapter
import com.bll.lnkteacher.ui.adapter.ExamScoreAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class ExamCorrectActivity:BaseDrawingActivity(),IContractView.IExamCorrectView,IFileUploadView{

    private var id=0
    private var classId=0
    private var className=""
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private val mPresenter= ExamCorrectPresenter(this,3)
    private var userItems= mutableListOf<ExamClassUserList.UserBean>()

    private var mAdapter: ExamCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages:Array<String>?=null
    private val commitItems = mutableListOf<ItemList>()
    private var initScores=mutableListOf<ExamScoreItem>()//初始题目分数
    private var examScoreItems= mutableListOf<ExamScoreItem>()//已填题目分数

    private var mScoreAdapter:ExamScoreAdapter?=null

    override fun onToken(token: String) {
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    url=ToolUtils.getImagesStr(urls)
                    val map= HashMap<String, Any>()
                    map["id"]=userItems[posUser].id
                    map["schoolExamJobId"]=userItems[posUser].schoolExamJobId
                    map["score"]=et_score_num.text.toString().toInt()
                    map["teacherUrl"]=url
                    map["classId"]=userItems[posUser].classId
                    map["status"]=2
                    map["question"]=Gson().toJson(examScoreItems)
                    mPresenter.onExamCorrectComplete(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onExamClassUser(classUserList: ExamClassUserList) {
        userItems.clear()
        for (item in classUserList.list){
            if (item.studentUrl.isNullOrEmpty()){
                item.status=3
            }
            else{
                item.status=1
            }
            if (item.teacherUrl.isNotEmpty()){
                item.status=2
            }
            userItems.add(item)
        }
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        mAdapter?.setNewData(userItems)
    }

    override fun onCorrectSuccess() {
        showToast(userItems[posUser].studentName+getString(R.string.teaching_correct_success))
        userItems[posUser].score=et_score_num.text.toString().toInt()
        userItems[posUser].teacherUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=Gson().toJson(examScoreItems)
        mAdapter?.notifyItemChanged(posUser)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik_a?.setPWEnabled(false,false)
        elik_b?.setPWEnabled(false,false)
        EventBus.getDefault().post(Constants.EXAM_CORRECT_EVENT)
    }

    
    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        id=intent.getIntExtra("id",0)
        classId=intent.getIntExtra("classId",0)
        className= intent.getStringExtra("className").toString()

        val map=HashMap<String,Any>()
        map["schoolExamJobId"]=id
        map["classId"]=classId
        mPresenter.getExamClassUser(map)
    }

    override fun initView() {
        elik_b?.setPWEnabled(false,false)
        disMissView(iv_tool,iv_catalog)

        setPageTitle("考试批改  $className")
        disMissView(ll_record)

        initRecyclerView()
        initRecyclerViewScore()

        tv_save.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1&&et_score_num.text.isNotEmpty()){
                for (ite in initScores){
                    if (!ite.score.isNullOrEmpty()){
                        examScoreItems.add(ite)
                    }
                }
                showLoading()
                commitPapers()
            }
            hideKeyboard()
        }

        iv_add.setOnClickListener {
            for (i in 0..1){
                initScores.add(ExamScoreItem().apply {
                    sort=initScores.size+1
                })
            }
            mScoreAdapter?.notifyItemRangeInserted(initScores.size-2,2)
            rv_list_score.scrollToPosition(initScores.size-1)
        }

        initScoreData()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,7)//创建布局管理
        mAdapter = ExamCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(7, 0, 20))
            setOnItemClickListener { adapter, view, position ->
                if (position==posUser)
                    return@setOnItemClickListener
                userItems[posUser].isCheck=false
                posUser=position//设置当前学生下标
                val item=userItems[position]
                item.isCheck=true
                notifyDataSetChanged()
                posImage=0
                setContentView()
            }
        }
    }

    private fun initRecyclerViewScore(){
        rv_list_score.layoutManager = GridLayoutManager(this,5)
        mScoreAdapter = ExamScoreAdapter(R.layout.item_exam_score, null).apply {
            rv_list_score.adapter = this
            bindToRecyclerView(rv_list_score)
        }
    }

    /**
     * 初始化分数列表数据
     */
    private fun initScoreData(){
        initScores.clear()
        for (i in 1..10){
            initScores.add(ExamScoreItem().apply {
                sort=i
            })
        }
        mScoreAdapter?.setNewData(initScores)
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        setContentImage()
    }

    override fun onPageUp() {
        if (isExpand){
            if (posImage>1){
                posImage-=2
            }
            else if (posImage==1){
                posImage=0
            }
        }
        else{
            if (posImage > 0) {
                posImage -= 1
            }
        }
        setContentImage()
    }

    override fun onPageDown() {
        if (isExpand){
            if (posImage<getImageSize()-2){
                posImage+=2
            }
            else if (posImage==getImageSize()-2){
                posImage=getImageSize()-1
            }
        }
        else{
            if (posImage < getImageSize() - 1) {
                posImage += 1
            }
        }
        setContentImage()
    }

    private fun setFocusableEdit(boolean: Boolean){
        et_score_num.isFocusable = boolean
        et_score_num.isFocusableInTouchMode = boolean
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        val userItem=userItems[posUser]
        when(userItem.status){
            1,3->{
                initScoreData()
            }
            2->{
                if (!userItem.question.isNullOrEmpty()){
                    initScores=Gson().fromJson(userItem.question, object : TypeToken<List<ExamScoreItem>>() {}.type) as MutableList<ExamScoreItem>
                    mScoreAdapter?.setNewData(initScores)
                }
            }
        }
        if (isExpand){
            isExpand=false
            onChangeExpandView()
        }

        when(userItem.status){
            1->{
                showView(ll_score)
                currentImages=userItem.studentUrl.split(",").toTypedArray()
                setFocusableEdit(true)
                showView(ll_score,rv_list_score,iv_add,tv_save)
                loadPapers()
            }
            2->{
                currentImages=userItem.teacherUrl.split(",").toTypedArray()
                showView(ll_score)
                et_score_num.setText(userItem.score.toString())
                setFocusableEdit(false)
                showView(ll_score,rv_list_score,iv_add)
                disMissView(tv_save)
                setContentImage()
            }
            3->{
                currentImages= arrayOf()
                disMissView(ll_score,rv_list_score,iv_add)
                et_score_num.setText("")
                setFocusableEdit(false)
                v_content_a.setImageResource(0)
                v_content_b.setImageResource(0)
                elik_a?.setPWEnabled(false,false)
                elik_b?.setPWEnabled(false,false)
            }
        }
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
        if (isExpand){
            when(userItems[posUser].status){
                1->{
                    elik_a?.setPWEnabled(true,true)
                    elik_b?.setPWEnabled(true,true)

                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_a)
                    tv_page_a.text="${posImage+1}"
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_a?.setLoadFilePath(drawPath, true)

                    if (posImage+1<getImageSize()){
                        val masterImage_b="${getPath()}/${posImage+1+1}.png"//原图
                        GlideUtils.setImageFile(this,File(masterImage_b),v_content_b)
                        tv_page.text="${posImage+1+1}"
                        val drawPath_b = getPathDrawStr(posImage+1+1)
                        elik_b?.setLoadFilePath(drawPath_b, true)
                    }
                    else{
                        elik_b?.setPWEnabled(false,false)
                        v_content_b.setImageResource(0)
                        tv_page.text=""
                    }
                }
                2->{
                    elik_a?.setPWEnabled(false,false)
                    elik_b?.setPWEnabled(false,false)

                    GlideUtils.setImageUrl(this, currentImages?.get(posImage) ,v_content_a)
                    tv_page_a.text="${posImage+1}"
                    if (posImage+1<getImageSize()){
                        GlideUtils.setImageUrl(this, currentImages?.get(posImage+1) ,v_content_b)
                        tv_page.text="${posImage+1+1}"
                    }
                    else{
                        v_content_b.setImageResource(0)
                        tv_page.text=""
                    }
                }
            }
        }
        else{
            when(userItems[posUser].status){
                1->{
                    elik_b?.setPWEnabled(true,true)
                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_b)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_b?.setLoadFilePath(drawPath, true)
                    tv_page.text="${posImage+1}"
                }
                2->{
                    elik_b?.setPWEnabled(false,false)
                    GlideUtils.setImageUrl(this, currentImages?.get(posImage) ,v_content_b)
                    tv_page.text="${posImage+1}"
                }
            }
        }
    }

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers(){
        showLoading()
        val savePaths= mutableListOf<String>()
        for (i in currentImages?.indices!!){
            savePaths.add(getPath()+"/${i+1}.png")
        }
        val files = FileUtils.getFiles(getPath())
        if (files.isNullOrEmpty()) {
            FileMultitaskDownManager.with(this).create(currentImages?.toMutableList()).setPath(savePaths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        hideLoading()
                        setContentImage()
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        hideLoading()
                    }
                })
        } else {
            hideLoading()
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
     * 提交学生考卷
     */
    private fun commitPapers(){
        commitItems.clear()
        //手写,图片合图
        for (i in currentImages?.indices!!){
            val index=i+1
            val masterImage="${getPath()}/${index}.png"//原图
            val drawPath = getPathDrawStr(index).replace("tch","png")
            val mergePath = getPath()//合并后的路径
            var mergePathStr = "${getPath()}/merge${index}.png"//合并后图片地址
            Thread {
                val oldBitmap = BitmapFactory.decodeFile(masterImage)
                val drawBitmap = BitmapFactory.decodeFile(drawPath)
                if (drawBitmap!=null){
                    val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                    BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge${index}")
                }
                else{
                    mergePathStr=masterImage
                }
                commitItems.add(ItemList().apply {
                    id = i
                    url = mergePathStr
                })
                if (commitItems.size==currentImages?.size){
                    commitItems.sort()
                    mUploadPresenter.getToken()
                }
            }.start()
        }
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathExam(id,classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }



}