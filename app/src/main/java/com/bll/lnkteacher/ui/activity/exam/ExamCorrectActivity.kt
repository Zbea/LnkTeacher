package com.bll.lnkteacher.ui.activity.exam

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.presenter.ExamCorrectPresenter
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.ExamCorrectUserAdapter
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import com.bll.lnkteacher.utils.FileMultitaskDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_testpaper_correct.ll_record
import kotlinx.android.synthetic.main.ac_testpaper_correct.ll_score
import kotlinx.android.synthetic.main.ac_testpaper_correct.ll_score_topic
import kotlinx.android.synthetic.main.ac_testpaper_correct.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_answer
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_save
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class ExamCorrectActivity:BaseDrawingActivity(),IContractView.IExamCorrectView,IFileUploadView{

    private var examBean: ExamCorrectList.ExamCorrectBean?=null
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private val mPresenter= ExamCorrectPresenter(this,3)
    private var userItems= mutableListOf<ExamClassUserList.ClassUserBean>()

    private var mAdapter: ExamCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages= mutableListOf<String>()
    private var tokenStr=""

    override fun onToken(token: String) {
        tokenStr=token
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
        userItems[posUser].score=tv_total_score.text.toString().toDouble()
        userItems[posUser].teacherUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=Gson().toJson(currentScores)
        mAdapter?.notifyItemChanged(posUser)
        disMissView(tv_save)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        setPWEnabled(false)
    }

    
    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamCorrectList.ExamCorrectBean
        correctModule=examBean?.questionType!!
        scoreMode=examBean?.questionMode!!
        if (!examBean?.answerUrl.isNullOrEmpty())
            answerImages= examBean?.answerUrl?.split(",") as MutableList<String>

        mUploadPresenter.getToken(false)
        fetchData()
    }

    override fun initView() {
        setPageTitle("考卷批改  ${examBean?.examName}  ${examBean?.className}")
        disMissView(iv_catalog,iv_btn,ll_record)

        if (answerImages.size>0){
            showView(tv_answer)
            setAnswerView()
        }

        tv_total_score.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1){
                NumberDialog(this,getCurrentScreenPos(),"请输入总分").builder().setDialogClickListener{
                    tv_total_score.text= it.toString()
                }
            }
        }

        tv_save.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1&& tv_total_score.text.toString().isNotEmpty()){
                showLoading()
                //没有手写，直接提交
                if (!FileUtils.isExistContent(getPathDraw())){
                    url=userItems[posUser].studentUrl
                    commit()
                }
                else{
                    commitPaper()
                }
            }
        }

        initRecyclerView()
        initRecyclerViewScore()

        onChangeExpandView()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,7)//创建布局管理
        mAdapter = ExamCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(7,  30))
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

    override fun onChangeExpandContent() {
        if (getImageSize()==1)
            return
        if (posImage>=getImageSize()-1&&getImageSize()>1)
            posImage=getImageSize()-2
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=if (isExpand)2 else 1
        }
        onChangeContent()
    }

    override fun onPageDown() {
        if (posImage<getImageSize()-1){
            posImage+=if (isExpand)2 else 1
        }
        onChangeContent()
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        if (isExpand){
            isExpand=false
            onChangeExpandView()
        }

        val userItem=userItems[posUser]
        correctStatus=userItem.status

        when(correctStatus){
            1->{
                currentScores = jsonToList(examBean?.question!!) as MutableList<ScoreItem>
                currentImages=ToolUtils.getImages(userItem.studentUrl)
                tv_total_score.text = ""
                showView(ll_score,tv_save)
                loadPapers()
                setDisableTouchInput(false)
                setPWEnabled(true)
            }
            2->{
                currentScores = jsonToList(userItem.question!!) as MutableList<ScoreItem>
                currentImages=ToolUtils.getImages(userItem.teacherUrl)
                tv_total_score.text = userItem.score.toString()
                showView(ll_score)
                disMissView(tv_save)
                onChangeContent()
                setDisableTouchInput(true)
                setPWEnabled(true)
            }
            3->{
                currentImages= mutableListOf()
                disMissView(ll_score)
                tv_page.text=""
                tv_page_total.text=""
                v_content_a?.setImageResource(0)
                v_content_b?.setImageResource(0)
                setDisableTouchInput(true)
                setPWEnabled(false)
            }
        }

        if (correctModule>0&&correctStatus!=3){
            showView(ll_score_topic)
            if (correctModule<3){
                mTopicScoreAdapter?.setNewData(currentScores)
            }
            else{
                mTopicMultiAdapter?.setNewData(currentScores)
            }
        }
        else{
            disMissView(ll_score_topic)
        }
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent(){
        if (isExpand&&posImage>getImageSize()-2)
            posImage=getImageSize()-2
        if (isExpand&&posImage<0)
            posImage=0

        tv_page_total.text="${getImageSize()}"
        tv_page_total_a.text="${getImageSize()}"
        if (isExpand){
            when(correctStatus){
                1->{
                    val masterImage=getPathStr(posImage+1)//原图
                    GlideUtils.setImageUrl(this,File(masterImage).path,v_content_a)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_a?.setLoadFilePath(drawPath, true)

                    val masterImage_b=getPathStr(posImage+1+1)//原图
                    GlideUtils.setImageUrl(this,File(masterImage_b).path,v_content_b)
                    val drawPath_b = getPathDrawStr(posImage+1+1)
                    elik_b?.setLoadFilePath(drawPath_b, true)
                }
                2->{
                    GlideUtils.setImageUrl(this, currentImages[posImage],v_content_a)
                    GlideUtils.setImageUrl(this, currentImages[posImage+1],v_content_b)
                }
            }
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            when(correctStatus){
                1->{
                    val masterImage=getPathStr(posImage+1)//原图
                    GlideUtils.setImageUrl(this,File(masterImage).path,v_content_b)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_b?.setLoadFilePath(drawPath, true)
                }
                2->{
                    GlideUtils.setImageUrl(this, currentImages[posImage],v_content_b)
                }
            }
            tv_page.text="${posImage+1}"
        }
    }

    override fun onElikSava_a() {
        if (isExpand){
            Thread {
                BitmapUtils.saveScreenShot(this, v_content_a, getPathMergeStr(posImage+1))
            }.start()
        }
    }

    override fun onElikSava_b() {
        if (isExpand){
            Thread {
                BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(posImage+1+1))
            }.start()
        }
        else{
            Thread {
                BitmapUtils.saveScreenShot(this, v_content_b, getPathMergeStr(posImage+1))
            }.start()
        }
    }

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers(){
        showLoading()
        val savePaths= mutableListOf<String>()
        for (i in currentImages.indices){
            savePaths.add(getPathStr(i+1))
        }
        if (!FileUtils.isExistContent(getPath())) {
            FileMultitaskDownManager.with(this).create(currentImages).setPath(savePaths).startMultiTaskDownLoad(
                object : FileMultitaskDownManager.MultiTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        hideLoading()
                        onChangeContent()
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        hideLoading()
                    }
                })
        } else {
            hideLoading()
            onChangeContent()
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (currentImages.isEmpty())
            return 0
        return currentImages.size
    }

    /**
     * 上传图片
     */
    private fun commitPaper(){
        //获取合图的图片，没有手写的页面那原图
        val paths= mutableListOf<String>()
        for (i in currentImages.indices){
            val mergePath=getPathMergeStr(i+1)
            if (File(mergePath).exists()){
                paths.add(mergePath)
            }
            else{
                val path=getPathStr(i+1)
                paths.add(path)
            }
        }
        FileImageUploadManager(tokenStr, paths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    url=ToolUtils.getImagesStr(urls)
                    commit()
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                    mUploadPresenter.getToken(false)
                }
            })
        }
    }

    /**
     * 提交考卷
     */
    private fun commit(){
        val map= HashMap<String, Any>()
        map["id"]=userItems[posUser].id
        map["schoolExamJobId"]=userItems[posUser].schoolExamJobId
        map["score"]=tv_total_score.text.toString().toDouble()
        map["teacherUrl"]=url
        map["classId"]=userItems[posUser].classId
        map["status"]=2
        map["question"]= Gson().toJson(currentScores)
        mPresenter.onExamCorrectComplete(map)
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathExam(examBean?.id, examBean?.classId, userItems[posUser].userId)
    }
    /**
     * 得到当前原图地址
     */
    private fun getPathStr(index: Int):String{
        return getPath()+"/${index}.png"//手绘地址
    }

    /**
     * 得到当前手绘路径
     */
    private fun getPathDraw():String{
        return getPath()+"/draw/"//手绘地址
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw/${index}.png"//手绘地址
    }

    /**
     * 得到当前合图地址
     */
    private fun getPathMergeStr(index: Int):String{
        return getPath()+"/merge/${index}.png"//手绘地址
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["schoolExamJobId"]= examBean?.schoolExamJobId!!
        map["classId"]= examBean?.classId!!
        mPresenter.getExamClassUser(map)
    }

    override fun onRefreshData() {
        fetchData()
    }

}