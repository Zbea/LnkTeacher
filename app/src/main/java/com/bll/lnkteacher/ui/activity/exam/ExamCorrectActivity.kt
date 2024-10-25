package com.bll.lnkteacher.ui.activity.exam

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.mvp.model.ItemList
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
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
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
    private val commitItems = mutableListOf<ItemList>()

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
                    map["score"]=tv_total_score.text.toString().toInt()
                    map["teacherUrl"]=url
                    map["classId"]=userItems[posUser].classId
                    map["status"]=2
                    map["question"]=toJson(currentScores)
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
        userItems[posUser].score=tv_total_score.text.toString().toInt()
        userItems[posUser].teacherUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=toJson(currentScores)
        mAdapter?.notifyItemChanged(posUser)
        disMissView(tv_save)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik_a?.setPWEnabled(false,false)
        elik_b?.setPWEnabled(false,false)
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

        fetchData()
    }

    override fun initView() {
        setPageTitle("考卷批改  ${examBean?.examName}  ${examBean?.className}")
        elik_b?.setPWEnabled(false,false)
        disMissView(iv_tool,iv_catalog,iv_btn,ll_record)

        if (answerImages.size>0){
            showView(tv_answer)
            setAnswerView()
        }

        tv_total_score.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1){
                NumberDialog(this).builder().setDialogClickListener{
                    tv_total_score.text=it.toString()
                }
            }
        }

        tv_save.setOnClickListener {
            val item=userItems[posUser]
            if (item.status==1&& tv_total_score.text.toString().isNotEmpty()){
                showLoading()
                commitPapers()
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
            }
            2->{
                currentScores = jsonToList(userItem.question!!) as MutableList<ScoreItem>
                currentImages=ToolUtils.getImages(userItem.teacherUrl)
                tv_total_score.text = userItem.score.toString()
                showView(ll_score)
                disMissView(tv_save)
                onChangeContent()
            }
            3->{
                currentImages= mutableListOf()
                disMissView(ll_score)
                tv_page.text=""
                tv_page_total.text=""
                v_content_a?.setImageResource(0)
                v_content_b?.setImageResource(0)
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
                    elik_a?.setPWEnabled(true,true)
                    elik_b?.setPWEnabled(true,true)

                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_a)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_a?.setLoadFilePath(drawPath, true)

                    if (posImage+1<getImageSize()){
                        val masterImage_b="${getPath()}/${posImage+1+1}.png"//原图
                        GlideUtils.setImageFile(this,File(masterImage_b),v_content_b)
                        val drawPath_b = getPathDrawStr(posImage+1+1)
                        elik_b?.setLoadFilePath(drawPath_b, true)
                    }
                    else{
                        elik_b?.setPWEnabled(false,false)
                        v_content_b?.setImageResource(0)
                    }
                }
                2->{
                    elik_a?.setPWEnabled(false,false)
                    elik_b?.setPWEnabled(false,false)

                    GlideUtils.setImageUrl(this, currentImages[posImage],v_content_a)
                    if (posImage+1<getImageSize()){
                        GlideUtils.setImageUrl(this, currentImages[posImage+1],v_content_b)
                    }
                    else{
                        v_content_b?.setImageResource(0)
                    }
                }
            }
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            when(correctStatus){
                1->{
                    elik_b?.setPWEnabled(true,true)
                    val masterImage="${getPath()}/${posImage+1}.png"//原图
                    GlideUtils.setImageFile(this,File(masterImage),v_content_b)
                    val drawPath = getPathDrawStr(posImage+1)
                    elik_b?.setLoadFilePath(drawPath, true)
                }
                2->{
                    elik_b?.setPWEnabled(false,false)
                    GlideUtils.setImageUrl(this, currentImages[posImage],v_content_b)
                }
            }
            tv_page.text="${posImage+1}"
        }
    }

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers(){
        showLoading()
        val savePaths= mutableListOf<String>()
        for (i in currentImages.indices){
            savePaths.add(getPath()+"/${i+1}.png")
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
     * 提交学生考卷
     */
    private fun commitPapers(){
        commitItems.clear()
        //手写,图片合图
        for (i in currentImages.indices){
            val index=i+1
            val masterImage="${getPath()}/${index}.png"//原图
            val drawPath = getPathDrawStr(index)
            Thread {
                val oldBitmap = BitmapFactory.decodeFile(masterImage)
                val drawBitmap = BitmapFactory.decodeFile(drawPath)
                if (drawBitmap!=null){
                    val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                    BitmapUtils.saveBmpGallery(this, mergeBitmap, masterImage)
                }
                commitItems.add(ItemList().apply {
                    id = i
                    url = masterImage
                })
                if (commitItems.size==currentImages.size){
                    commitItems.sort()
                    runOnUiThread {
                        mUploadPresenter.getToken()
                    }
                }
            }.start()
        }
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathExam(examBean?.id, examBean?.classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.png"//手绘地址
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