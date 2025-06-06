package com.bll.lnkteacher.ui.activity.teaching

import android.media.MediaPlayer
import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.homework.ResultStandardItem
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.HomeworkResultStandardAdapter
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_homework_correct.iv_file
import kotlinx.android.synthetic.main.ac_homework_correct.iv_play
import kotlinx.android.synthetic.main.ac_homework_correct.iv_tips
import kotlinx.android.synthetic.main.ac_homework_correct.iv_tips_info
import kotlinx.android.synthetic.main.ac_homework_correct.ll_correct
import kotlinx.android.synthetic.main.ac_homework_correct.ll_play
import kotlinx.android.synthetic.main.ac_homework_correct.ll_progressbar
import kotlinx.android.synthetic.main.ac_homework_correct.ll_record
import kotlinx.android.synthetic.main.ac_homework_correct.progressBar
import kotlinx.android.synthetic.main.ac_homework_correct.rv_list
import kotlinx.android.synthetic.main.ac_homework_correct.rv_list_score
import kotlinx.android.synthetic.main.ac_homework_correct.tv_correct_module
import kotlinx.android.synthetic.main.ac_homework_correct.tv_end_time
import kotlinx.android.synthetic.main.ac_homework_correct.tv_play
import kotlinx.android.synthetic.main.ac_homework_correct.tv_save
import kotlinx.android.synthetic.main.ac_homework_correct.tv_share
import kotlinx.android.synthetic.main.ac_homework_correct.tv_start_time
import kotlinx.android.synthetic.main.ac_homework_correct.tv_take_time
import kotlinx.android.synthetic.main.ac_homework_correct.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.stream.Collectors

class HomeworkCorrectActivity : BaseDrawingActivity(), IContractView.ITestPaperCorrectDetailsView, IFileUploadView {

    private var mId = 0
    private var correctList: CorrectBean? = null
    private val mUploadPresenter = FileUploadPresenter(this, 3)
    private val mPresenter = TestPaperCorrectDetailsPresenter(this, 3)
    private var mClassBean: TestPaperClassBean? = null
    private var userItems = mutableListOf<TestPaperClassUserList.ClassUserBean>()
    private var mResultStandardAdapter: HomeworkResultStandardAdapter? = null
    private var resultStandardItems= mutableListOf<ResultStandardItem>()
    private var mAdapter: TestPaperCorrectUserAdapter? = null
    private var url = ""//上个学生提交地址
    private var posImage = 0//当前图片下标
    private var posUser = 0//当前学生下标
    private var currentImages = mutableListOf<String>()//当前学生作业地址
    private var recordPath = ""
    private var mediaPlayer: MediaPlayer? = null
    private var tokenStr = ""
    private var items=mutableListOf<ResultStandardItem>()
    private val results= mutableListOf<Int>()
    private var result=0
    private var timer: Timer?=null

    override fun onToken(token: String) {
        tokenStr = token
    }

    override fun onClassPapers(bean: TestPaperClassUserList) {
        userItems = bean.taskList

        if (userItems.size > 0) {
            userItems[posUser].isCheck = true
            mAdapter?.setNewData(userItems)

            Handler().post {
                setContentView()
            }
        }
    }

    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name + getString(R.string.teaching_correct_success))
        correctStatus = 2

        userItems[posUser].submitUrl = url
        userItems[posUser].status = 2
        userItems[posUser].score= result.toDouble()
        userItems[posUser].question=Gson().toJson(results)

        mAdapter?.notifyItemChanged(posUser)

        disMissView(tv_save)
        setDisableTouchInput(true)
        FileUtils.deleteFile(File(getPath()))
        EventBus.getDefault().post(if (correctList?.taskType == 1) Constants.HOMEWORK_CORRECT_EVENT else Constants.TESTPAPER_CORRECT_EVENT)
    }

    override fun onCompleteSuccess() {
        showToast("全部批改完成")
        fetchClassList()
        EventBus.getDefault().post(Constants.HOMEWORK_CORRECT_EVENT)
    }

    override fun onChangeQuestionType() {
        correctList?.questionType=correctModule
        disMissView(tv_correct_module)
    }

    override fun onShare() {
        showToast("分享成功")
    }

    override fun layoutId(): Int {
        return R.layout.ac_homework_correct
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT
        val classPos = intent.getIntExtra("classPos", -1)
        correctList = intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        mClassBean = correctList?.examList?.get(classPos)!!
        correctModule = if (correctList?.questionType!!<0)1 else correctList?.questionType!!

        mId = correctList?.id!!

        resultStandardItems=DataBeanManager.getResultStandardItems(correctList!!.subType,correctList!!.subTypeName,correctModule)

        fetchClassList()
    }

    override fun initView() {
        setPageTitle("作业批改  ${correctList?.title}  ${mClassBean?.name}")
        disMissView(iv_catalog, iv_btn)

        //如果是朗读本
        if (correctList?.subType == 3) {
            showView(ll_record)
            disMissView(ll_draw_content)
        } else {
            disMissView(ll_record)
            showView(ll_draw_content)
        }

        if (correctList?.questionType!!<0){
            showView(tv_correct_module)
        }
        else{
            disMissView(tv_correct_module)
        }

        tv_save.setOnClickListener {
            if (correctStatus == 1&&isCorrectComplete()) {
                showLoading()
                if (correctList?.subType==3){
                    commit()
                }
                else{
                    //没有手写，直接提交
                    if (!FileUtils.isExistContent(getPathDraw())) {
                        url = userItems[posUser].studentUrl
                        commit()
                    } else {
                        commitPaper()
                    }
                }
            }
        }

        iv_play.setOnClickListener {
            if (mediaPlayer != null) {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    timer?.cancel()
                    changeMediaView(false)
                } else {
                    mediaPlayer?.start()
                    startTimer()
                    changeMediaView(true)
                }
            }
        }

        tv_correct_module.setOnClickListener {
             if (correctModule==1){
                 correctModule =2
                 tv_correct_module.text="初级批改"
            } else{
                 correctModule=1
                 tv_correct_module.text="高级批改"
            }
            resultStandardItems=DataBeanManager.getResultStandardItems(correctList!!.subType,correctList!!.subTypeName,correctModule)
            initRecyclerViewResult()
            Handler().post {
                setContentView()
            }
        }

        iv_tips.setOnClickListener {
            showView(iv_tips_info)
        }

        iv_tips_info.setOnClickListener {
            disMissView(iv_tips_info)
        }

        tv_share.setOnClickListener {
            CommonDialog(this).setContent("确定分享该学生作业？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun ok() {
                    val userIds= mutableListOf<Int>()
                    for (item in userItems){
//                        if (userItems.indexOf(item)!=posUser)
                            userIds.add(item.userId)
                    }
                    val map=HashMap<String,Any>()
                    map["type"]=1
                    map["id"]=userItems[posUser].studentTaskId
                    map["userIds"]= userIds
                    mPresenter.share(map)
                }
            })
        }

        initRecyclerViewUser()
        initRecyclerViewResult()

        onChangeExpandView()
    }

    private fun initRecyclerViewUser() {
        rv_list.layoutManager = GridLayoutManager(this, 6)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, 1,correctModule,null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(6, 25))
            setOnItemClickListener { adapter, view, position ->
                if (position == posUser)
                    return@setOnItemClickListener

                release()

                val oldItem=userItems[posUser]
                oldItem.isCheck = false
                mAdapter?.notifyItemChanged(posUser)

                posUser = position//设置当前学生下标
                userItems[position].isCheck = true
                mAdapter?.notifyItemChanged(posUser)

                posImage = 0
                Handler().post {
                    setContentView()
                }
            }
        }
    }

    private fun initRecyclerViewResult() {
        val itemDeco=SpaceItemDeco(40)
        if (correctModule==2){
            val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(
                DP2PX.dip2px(this,20f), DP2PX.dip2px(this,30f),
                DP2PX.dip2px(this,20f), DP2PX.dip2px(this,30f))
            rv_list_score.layoutParams=layoutParams
        }
        else{
            val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(
                DP2PX.dip2px(this,80f), DP2PX.dip2px(this,30f),
                DP2PX.dip2px(this,80f), DP2PX.dip2px(this,30f))
            rv_list_score.layoutParams=layoutParams
        }
        val layoutManager=if (correctModule==2) GridLayoutManager(this,resultStandardItems.size) else LinearLayoutManager(this)
        val layoutResId=if (correctModule==2) R.layout.item_homework_result_standard_high else R.layout.item_homework_result_standard
        rv_list_score.layoutManager = layoutManager//创建布局管理
        mResultStandardAdapter = HomeworkResultStandardAdapter(layoutResId, correctModule,items).apply {
            rv_list_score.adapter = this
            bindToRecyclerView(rv_list_score)
            if (correctModule==2){
                if (rv_list_score.itemDecorationCount!=0){
                    rv_list_score.removeItemDecorationAt(0)
                }
            }else{
                if (rv_list_score.itemDecorationCount==0)
                    rv_list_score.addItemDecoration(itemDeco)
            }
            setCustomItemChildClickListener{ position,childPosition->
                val parentItem=items[position]
                for (item in parentItem.list){
                    item.isCheck=false
                }
                val childItem=parentItem.list[childPosition]
                childItem.isCheck=true

                parentItem.sort=childItem.sort
                parentItem.score=childItem.score

                notifyItemChanged(position)

                if (isCorrectComplete()){
                    results.clear()
                    var totalScores=0.0
                    for (item in items){
                        results.add(item.sort)
                        totalScores+=item.score
                    }
                    val averageScore=totalScores/items.size
                    getScore(averageScore)
                }
            }
        }
    }

    private fun getScore(averageScore:Double){
        if (correctModule==1){
            result = if (averageScore>=85){
                1
            } else if (averageScore>=70){
                2
            } else{
                3
            }
        }
        else{
            if (averageScore>=95){
                result=1
            }
            else if (averageScore>=90){
                result=2
            }
            else if (averageScore>=85){
                result=3
            }
            else if (averageScore>=80){
                result=4
            }
            else if (averageScore>=75){
                result=5
            }
            else if (averageScore>=70){
                result=6
            }
            else if (averageScore>=65){
                result=7
            }
            else if (averageScore>=60){
                result=8
            }
            else{
                result=9
            }
        }
        tv_total_score.text=DataBeanManager.getResultStandardStr(result.toDouble(),correctModule)
    }

    /**
     * 判断批改是否完成
     */
    private fun isCorrectComplete():Boolean{
        for (item in items){
            if (item.score<=0){
                return false
            }
        }
        return true
    }

    override fun onChangeExpandContent() {
        if (getImageSize() == 1)
            return
        changeErasure()
        isExpand = !isExpand
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= if (isExpand) 2 else 1
            onChangeContent()
        }
    }

    override fun onPageDown() {
        val count = if (isExpand) getImageSize() - 2 else getImageSize() - 1
        if (posImage < count) {
            posImage += if (isExpand) 2 else 1
            onChangeContent()
        }
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView() {
        val userItem = userItems[posUser]
        correctStatus = userItem.status

        tv_take_time.text = if (userItem.takeTime > 0) "用时：${DateUtils.longToMinute(userItem.takeTime)}分钟" else ""

        items=resultStandardItems.stream().collect(Collectors.toList())
        if (!userItem.question.isNullOrEmpty()&&userItem.question.length<20){
            val types=Gson().fromJson(userItem.question, object : TypeToken<MutableList<Int>>() {}.type) as MutableList<Int>
            if (types.size>0){
                for (item in items){
                    val i=items.indexOf(item)
                    val type=types[i]
                    item.list[type-1].isCheck=true
                }
            }
        }

        if (correctList?.subType == 3) {
            if (!userItem.studentUrl.isNullOrEmpty()) {
                recordPath = userItem.studentUrl

                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordPath)
                mediaPlayer?.setOnCompletionListener {
                    changeMediaView(false)
                    mediaPlayer?.seekTo(0)
                    progressBar.progress=0
                    tv_start_time.text="00:00"
                    timer?.cancel()
                }
                mediaPlayer?.prepare()
                tv_start_time.text="00:00"
                mediaPlayer?.seekTo(0)
                tv_end_time.text=DateUtils.secondToString(mediaPlayer?.duration!!)
                progressBar.max = mediaPlayer?.duration!!
                changeMediaView(false)

                showView(iv_file, ll_play,ll_progressbar)
            } else {
                disMissView(iv_file, ll_play,ll_progressbar)
            }
        }

        isDrawingSave=correctStatus==1

        when (correctStatus) {
            1 -> {
                currentImages = ToolUtils.getImages(userItem.studentUrl)
                showView(ll_correct,tv_save)
                disMissView(tv_share)
                tv_total_score.text=""

                setDisableTouchInput(false)
                setPWEnabled(true)
            }
            2 -> {
                currentImages = ToolUtils.getImages(userItem.submitUrl)
                tv_total_score.text=DataBeanManager.getResultStandardStr(userItem.score,correctModule)
                if (correctList?.subType!=3)
                    showView(tv_share)
                showView(ll_correct)
                disMissView(tv_save)

                setDisableTouchInput(true)
                setPWEnabled(false)
            }
            3 -> {
                currentScores.clear()
                currentImages = mutableListOf()
                disMissView(ll_correct)

                setDisableTouchInput(true)
                setPWEnabled(false)
            }
        }

        mResultStandardAdapter?.setNewData(items)

        onChangeContent()
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent() {
        if (isExpand && posImage > getImageSize() - 2)
            posImage = getImageSize() - 2
        if (isExpand && posImage < 0)
            posImage = 0

        tv_page_total.text = "${getImageSize()}"
        tv_page_total_a.text = "${getImageSize()}"

        if (currentImages.size==0){
            v_content_a?.setImageResource(0)
            v_content_a?.setImageResource(0)
            return
        }

        if (isExpand) {
            GlideUtils.setImageCacheUrl(this, currentImages[posImage], v_content_a)
            GlideUtils.setImageCacheUrl(this, currentImages[posImage + 1], v_content_b)
            if (correctStatus == 1) {
                val drawPath = getPathDrawStr(posImage)
                elik_a?.setLoadFilePath(drawPath, true)

                val drawPath_b = getPathDrawStr(posImage + 1)
                elik_b?.setLoadFilePath(drawPath_b, true)
            }
            tv_page.text = "${posImage + 1}"
            tv_page_a.text = "${posImage + 1 + 1}"
        } else {
            GlideUtils.setImageCacheUrl(this, currentImages[posImage], v_content_b)
            if (correctStatus == 1) {
                val drawPath = getPathDrawStr(posImage)
                elik_b?.setLoadFilePath(drawPath, true)
            }
            tv_page.text = "${posImage + 1}"
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize(): Int {
        if (currentImages.isEmpty())
            return 0
        return currentImages.size
    }

    override fun onElikSava_a() {
        if (isExpand)
           BitmapUtils.saveScreenShot(v_content_a, getPathMergeStr(posImage))
    }

    override fun onElikSava_b() {
        if (isExpand){
            BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(posImage+1))
        }
        else{
            BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(posImage))
        }
    }

    /**
     * 上传图片
     */
    private fun commitPaper() {
        //获取合图的图片，没有手写的页面那原图
        val paths = mutableListOf<String>()
        for (i in currentImages.indices) {
            val mergePath = getPathMergeStr(i)
            if (File(mergePath).exists()) {
                paths.add(mergePath)
            }
        }
        FileImageUploadManager(tokenStr, paths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    //校验正确图片，没有手写图片拿原图
                    val uploadPaths = mutableListOf<String>()
                    var index = 0
                    for (i in currentImages.indices) {
                        val mergePath = getPathMergeStr(i)
                        if (File(mergePath).exists()) {
                            uploadPaths.add(urls[index])
                            index += 1
                        } else {
                            uploadPaths.add(currentImages[i])
                        }
                    }
                    url = ToolUtils.getImagesStr(uploadPaths)
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
     * 提交学生考卷
     */
    private fun commit() {
        val map = HashMap<String, Any>()
        map["studentTaskId"] = userItems[posUser].studentTaskId
        if (correctList?.subType!=3)
            map["submitUrl"] = url
        map["status"] = 2
        map["score"] = result
        map["question"]=Gson().toJson(results)
        mPresenter.commitPaperStudent(map)

        if (correctList?.questionType!!<0)
            mPresenter.changeQuestionType(mId,correctModule)
    }

    /**
     * 获取班级学生列表
     */
    private fun fetchClassList() {
        mUploadPresenter.getToken(false)
        mPresenter.getPaperClassPapers(mId, mClassBean?.classId!!)
    }

    /**
     * 文件路径
     */
    private fun getPath(): String {
        return FileAddress().getPathHomework(mId, mClassBean?.classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘路径
     */
    private fun getPathDraw(): String {
        return getPath() + "/draw/"//手绘地址
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int): String {
        return getPath() + "/draw/${index + 1}.png"//手绘地址
    }

    /**
     * 得到当前合图地址
     */
    private fun getPathMergeStr(index: Int): String {
        return getPath() + "/merge/${index + 1}.png"//手绘地址
    }

    /**
     * 更改播放view状态
     */
    private fun changeMediaView(boolean: Boolean) {
        if (boolean) {
            iv_play.setImageResource(R.mipmap.icon_record_pause)
            tv_play.setText(R.string.pause_record)
        } else {
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.setText(R.string.play_record)
        }
    }

    private fun startTimer(){
        Thread {
            timer= Timer()
            timer!!.schedule(object: TimerTask() {
                override fun run() {
                    runOnUiThread {
                        progressBar.progress=mediaPlayer?.currentPosition!!
                        tv_start_time.text=DateUtils.secondToString(mediaPlayer?.currentPosition!!)
                    }
                }
            } ,500,500)
        }.start()
    }


    private fun release() {
        timer?.cancel()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onRefreshData() {
        fetchClassList()
    }
}