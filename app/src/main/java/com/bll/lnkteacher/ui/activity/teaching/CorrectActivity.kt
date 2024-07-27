package com.bll.lnkteacher.ui.activity.teaching

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_answer.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class CorrectActivity:BaseDrawingActivity(),IContractView.ITestPaperCorrectDetailsView,IFileUploadView{

    private var mId=0
    private var correctList:CorrectBean?=null
    private val mUploadPresenter=FileUploadPresenter(this,3)
    private val mPresenter=TestPaperCorrectDetailsPresenter(this,3)
    private var mClassBean: TestPaperClassBean?=null
    private var userItems= mutableListOf<TestPaperClassUserList.ClassUserBean>()

    private var mAdapter:TestPaperCorrectUserAdapter?=null
    private var url=""//上个学生提交地址
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages= mutableListOf<String>()//当前学生作业地址
    private val commitItems = mutableListOf<ItemList>()
    private var recordPath = ""
    private var mediaPlayer: MediaPlayer? = null

    private var userCorrect=0
    private var userSend=0


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
                    map["studentTaskId"]=userItems[posUser].studentTaskId
                    map["score"]=tv_total_score.text.toString().toInt()
                    map["submitUrl"]=url
                    map["status"]=2
                    map["question"]=toJson(currentScores)
                    mPresenter.commitPaperStudent(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onClassPapers(bean: TestPaperClassUserList) {
        userItems=bean.list
        userCorrect=bean.totalUpdate
        userSend=bean.totalSend
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        mAdapter?.setNewData(userItems)
    }
    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name+getString(R.string.teaching_correct_success))
        userCorrect+=1
        userItems[posUser].score=tv_total_score.text.toString().toInt()
        userItems[posUser].submitUrl=url
        userItems[posUser].status=2
        userItems[posUser].question=toJson(currentScores)
        mAdapter?.notifyItemChanged(posUser)
        disMissView(tv_save)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik_a?.setPWEnabled(false,false)
        elik_b?.setPWEnabled(false,false)
        EventBus.getDefault().post(if (correctList?.taskType==1)Constants.HOMEWORK_CORRECT_EVENT else Constants.EXAM_CORRECT_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        val classPos=intent.getIntExtra("classPos",-1)
        correctList= intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        mClassBean= correctList?.examList?.get(classPos)!!
        scoreMode=correctList?.questionMode!!
        correctModule=correctList?.questionType!!
        mId=correctList?.id!!

        if (!correctList?.answerUrl.isNullOrEmpty()){
            answerImages=ToolUtils.getImages(correctList?.answerUrl)
        }

        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun initView() {
        val title=if (correctList?.taskType==1) "作业批改" else "试卷批改"
        setPageTitle("$title  ${mClassBean?.examName}  ${mClassBean?.name}")

        elik_b?.setPWEnabled(false,false)
        disMissView(iv_tool,iv_catalog,iv_btn)

        if (answerImages.size>0){
            setPageSetting("标准答案")
            setAnswerView()
        }

        //如果是朗读本
        if (correctList?.subType!! == 3) {
            showView(ll_record)
            disMissView(ll_draw_content,ll_score_topic,ll_score)
        } else {
            disMissView(ll_record)
            showView(ll_draw_content)
            tv_score_label.text=if (scoreMode==1) "赋分批改框" else "对错批改框"
        }

        tv_setting.setOnClickListener {
            showView(rl_answer)
        }

        iv_close_answer.setOnClickListener {
            disMissView(rl_answer)
        }

        tv_save.setOnClickListener {
            if (correctStatus==1&& tv_total_score.text.toString().isNotEmpty()){
                showLoading()
                commitPapers()
            }
            hideKeyboard()
        }

        tv_total_score.setOnClickListener {
            if (correctStatus==1){
                NumberDialog(this).builder().setDialogClickListener{
                    tv_total_score.text=it.toString()
                }
            }
        }

        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0,DP2PX.dip2px(this,100f))
        }

        iv_play.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordPath)
                mediaPlayer?.setOnCompletionListener {
                    changeMediaView(false)
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                changeMediaView(true)
            }
            else{
                if (mediaPlayer?.isPlaying == true){
                    mediaPlayer?.pause()
                    changeMediaView(false)
                }
                else{
                    mediaPlayer?.start()
                    changeMediaView(true)
                }
            }
        }

        initRecyclerViewUser()
        initRecyclerViewScore()

        onChangeExpandView()
    }

    private fun initRecyclerViewUser(){
        rv_list.layoutManager = GridLayoutManager(this,7)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(7,  15))
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
        correctStatus=userItems[posUser].status

        if (correctList?.subType==3){
            if (!userItem.studentUrl.isNullOrEmpty()) {
                recordPath = userItem.studentUrl.split(",")[0]
                showView(iv_file, ll_play)
            } else {
                disMissView(iv_file, ll_play)
            }
            return
        }

        when(correctStatus){
            1->{
                if (correctList?.question?.isNotEmpty() == true&&correctModule>0){
                    currentScores= jsonToList(correctList?.question!!) as MutableList<ScoreItem>
                }
                tv_total_score.text = "0"
                currentImages=ToolUtils.getImages(userItem.studentUrl)
                showView(ll_score,tv_save)
                loadPapers()
            }
            2->{
                currentImages=ToolUtils.getImages(userItem.submitUrl)
                if (userItem.question?.isNotEmpty() == true&&correctModule>0){
                    currentScores= jsonToList(userItem.question) as MutableList<ScoreItem>
                }
                tv_total_score.text = userItem.score.toString()
                showView(ll_score)
                disMissView(tv_save)
                onChangeContent()
            }
            3->{
                currentImages=ToolUtils.getImages(correctList?.examUrl)
                disMissView(ll_score)
                v_content_a?.setImageResource(0)
                v_content_b?.setImageResource(0)
                elik_a?.setPWEnabled(false,false)
                elik_b?.setPWEnabled(false,false)
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
        val files = FileUtils.getAscFiles(getPath())
        if (files.isNullOrEmpty()) {
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
                if (commitItems.size==currentImages.size){
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
        val path=if (correctList?.taskType==1){
            FileAddress().getPathHomework(mId,mClassBean?.classId, userItems[posUser].userId)
        }
        else{
            FileAddress().getPathTestPaperDrawing(mId,mClassBean?.classId, userItems[posUser].userId)
        }
        return path
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

    /**
     * 更改播放view状态
     */
    private fun changeMediaView(boolean: Boolean){
        if (boolean){
            iv_play.setImageResource(R.mipmap.icon_record_pause)
            tv_play.setText(R.string.pause)
        }
        else{
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.setText(R.string.play)
        }
    }


    private fun release(){
        if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
        release()
    }

}