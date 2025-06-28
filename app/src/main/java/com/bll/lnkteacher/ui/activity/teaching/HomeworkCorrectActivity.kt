package com.bll.lnkteacher.ui.activity.teaching

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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
import kotlinx.android.synthetic.main.ac_homework_correct.ll_score
import kotlinx.android.synthetic.main.ac_homework_correct.ll_speed
import kotlinx.android.synthetic.main.ac_homework_correct.progressBar
import kotlinx.android.synthetic.main.ac_homework_correct.ratingBar
import kotlinx.android.synthetic.main.ac_homework_correct.rv_list
import kotlinx.android.synthetic.main.ac_homework_correct.rv_list_score
import kotlinx.android.synthetic.main.ac_homework_correct.tv_correct_module
import kotlinx.android.synthetic.main.ac_homework_correct.tv_end_time
import kotlinx.android.synthetic.main.ac_homework_correct.tv_play
import kotlinx.android.synthetic.main.ac_homework_correct.tv_save
import kotlinx.android.synthetic.main.ac_homework_correct.tv_share
import kotlinx.android.synthetic.main.ac_homework_correct.tv_speed_0_5
import kotlinx.android.synthetic.main.ac_homework_correct.tv_speed_1
import kotlinx.android.synthetic.main.ac_homework_correct.tv_speed_1_5
import kotlinx.android.synthetic.main.ac_homework_correct.tv_speed_2
import kotlinx.android.synthetic.main.ac_homework_correct.tv_speed_2_5
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

class HomeworkCorrectActivity : BaseDrawingActivity(), IContractView.ITestPaperCorrectDetailsView, IFileUploadView {

    private var mId = 0
    private var subType = 0
    private var correctList: CorrectBean? = null
    private val mUploadPresenter = FileUploadPresenter(this, 3)
    private val mPresenter = TestPaperCorrectDetailsPresenter(this, 3)
    private var mClassBean: TestPaperClassBean? = null
    private var userItems = mutableListOf<TestPaperClassUserList.ClassUserBean>()
    private var mResultStandardAdapter: HomeworkResultStandardAdapter? = null
    private var mAdapter: TestPaperCorrectUserAdapter? = null
    private var url = ""//上个学生提交地址
    private var posImage = 0//当前图片下标
    private var posUser = 0//当前学生下标
    private var currentImages = mutableListOf<String>()//当前学生作业地址
    private var recordPath = ""
    private var tokenStr = ""
    private var items = mutableListOf<ResultStandardItem>()
    private val results = mutableListOf<Int>()
    private var score = 0
    private var exoPlayer: ExoPlayer? = null
    private var timer: Timer? = null
    private var speed=1f
    private var isReadyRecorder=false

    override fun onToken(token: String) {
        tokenStr = token
    }

    override fun onClassPapers(bean: TestPaperClassUserList) {
        userItems = bean.taskList

        if (userItems.size > 0) {
            userItems[posUser].isCheck = true
            mAdapter?.setNewData(userItems)

            setContentView()
        }
    }

    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name + getString(R.string.teaching_correct_success))
        correctStatus = 2

        userItems[posUser].submitUrl = url
        userItems[posUser].status = 2
        userItems[posUser].score = score.toDouble()
        userItems[posUser].question = Gson().toJson(results)

        mAdapter?.notifyItemChanged(posUser)

        disMissView(tv_save)
        if (subType != 3)
            showView(tv_share)
        setDisableTouchInput(true)
        FileUtils.deleteFile(File(getPath()))
        EventBus.getDefault().post(Constants.HOMEWORK_CORRECT_EVENT)
    }

    override fun onCompleteSuccess() {
        showToast("全部批改完成")
        fetchClassList()
        EventBus.getDefault().post(Constants.HOMEWORK_CORRECT_EVENT)
    }

    override fun onChangeQuestionType() {
        correctList?.questionType = correctModule
        disMissView(tv_correct_module)
    }

    override fun onShare() {
        showToast("分享成功")
        userItems[posUser].shareType = 1
        mAdapter?.notifyItemChanged(posUser)
    }

    override fun layoutId(): Int {
        return R.layout.ac_homework_correct
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT
        val classPos = intent.getIntExtra("classPos", -1)
        correctList = intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        mClassBean = correctList?.examList?.get(classPos)!!
        correctModule = if (correctList?.questionType!! < 0) 1 else correctList?.questionType!!
        mId = correctList?.id!!
        subType = correctList?.subType!!

        fetchClassList()
    }

    override fun initView() {
        setPageTitle("作业批改  ${correctList?.title}  ${mClassBean?.name}")
        disMissView(iv_catalog)
        iv_btn.setImageResource(R.mipmap.icon_draw_image_zoom)

        if (correctList?.questionType!! < 0) {
            showView(tv_correct_module)
        } else {
            disMissView(tv_correct_module)
        }

        //如果是朗读本
        if (subType == 3) {
            showView(ll_record)
            disMissView(ll_draw_content)
        }

        //预习本
        if (subType == 10) {
            showView(ratingBar)
            disMissView(rv_list_score, tv_correct_module, iv_tips)
        }

        iv_btn.setOnClickListener {
            MethodManager.gotoFullImage(this,ToolUtils.getImagesStr(currentImages),getPath())
        }

        tv_save.setOnClickListener {
            if (correctStatus == 1 && score > 0) {
                showLoading()
                if (subType == 3) {
                    commit()
                } else {
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
            if (exoPlayer != null) {
                if (!isReadyRecorder){
                    showToast(1,"录音未加载完成")
                    return@setOnClickListener
                }
                if (exoPlayer?.isPlaying == true) {
                    exoPlayer?.pause()
                    timer?.cancel()
                    changeMediaView(false)
                } else {
                    exoPlayer?.play()
                    startTimer()
                    changeMediaView(true)
                }
            }
        }

        tv_speed_0_5.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(0.5f)
                setSpeedView(tv_speed_0_5)
            }
        }
        tv_speed_1.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(1f)
                setSpeedView(tv_speed_1)
            }
        }
        tv_speed_1_5.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(1.5f)
                setSpeedView(tv_speed_1_5)
            }
        }
        tv_speed_2.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(2f)
                setSpeedView(tv_speed_2)
            }
        }
        tv_speed_2_5.setOnClickListener {
            if (exoPlayer != null) {
                setSpeed(2.5f)
                setSpeedView(tv_speed_2_5)
            }
        }

        tv_correct_module.setOnClickListener {
            if (correctModule == 1) {
                correctModule = 2
                tv_correct_module.text = "初级批改"
            } else {
                correctModule = 1
                tv_correct_module.text = "高级批改"
            }
            initRecyclerViewResult()
            setContentView()
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
                    val userIds = mutableListOf<Int>()
                    for (item in userItems) {
                        if (userItems.indexOf(item) != posUser)
                            userIds.add(item.userId)
                    }
                    if (userIds.isEmpty()) {
                        showToast(2, "暂无可分享学生")
                    } else {
                        val map = HashMap<String, Any>()
                        map["type"] = 1
                        map["id"] = userItems[posUser].studentTaskId
                        map["userIds"] = userIds
                        mPresenter.share(map)
                    }
                }
            })
        }

        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            score = fl.toInt()
        }

        initRecyclerViewUser()
        if (subType != 10)
            initRecyclerViewResult()

        onChangeExpandView()
    }

    private fun setSpeedView(tvSpeed: TextView) {
        tv_speed_0_5.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_0_5.setTextColor(getColor(R.color.black))
        tv_speed_1.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_1.setTextColor(getColor(R.color.black))
        tv_speed_1_5.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_1_5.setTextColor(getColor(R.color.black))
        tv_speed_2.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_2.setTextColor(getColor(R.color.black))
        tv_speed_2_5.background = getDrawable(R.drawable.bg_black_stroke_5dp_corner)
        tv_speed_2_5.setTextColor(getColor(R.color.black))
        tvSpeed.background = getDrawable(R.drawable.bg_black_solid_5dp_corner)
        tvSpeed.setTextColor(getColor(R.color.white))
    }

    private fun setSpeed(speed: Float) {
        this.speed=speed
        exoPlayer?.setPlaybackSpeed(speed)
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
            timer?.cancel()
            exoPlayer?.play()
            startTimer()
        }
    }

    /**
     * 初始化数据
     */
    private fun getInitResultStandardItems(): MutableList<ResultStandardItem> {
        return DataBeanManager.getResultStandardItems(subType, correctList!!.subTypeName, correctModule)
    }

    private fun initRecyclerViewUser() {
        rv_list.layoutManager = GridLayoutManager(this, 6)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, if (subType == 10) 3 else 1, correctModule, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(6, 25))
            setOnItemClickListener { adapter, view, position ->
                if (position == posUser)
                    return@setOnItemClickListener

                release()

                val oldItem = userItems[posUser]
                oldItem.isCheck = false
                mAdapter?.notifyItemChanged(posUser)

                posUser = position//设置当前学生下标
                userItems[position].isCheck = true
                mAdapter?.notifyItemChanged(posUser)

                posImage = 0
                setContentView()
            }
        }
    }

    private fun initRecyclerViewResult() {
        val itemDeco = SpaceItemDeco(50)

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, if (correctModule == 2) 20f else 80f), DP2PX.dip2px(this, 30f),
            DP2PX.dip2px(this, if (correctModule == 2) 20f else 80f), DP2PX.dip2px(this, 30f)
        )
        rv_list_score.layoutParams = layoutParams

        val layoutManager = if (correctModule == 2) GridLayoutManager(this, getInitResultStandardItems().size) else LinearLayoutManager(this)
        val layoutResId = if (correctModule == 2) R.layout.item_homework_result_standard_high else R.layout.item_homework_result_standard
        rv_list_score.layoutManager = layoutManager//创建布局管理
        mResultStandardAdapter = HomeworkResultStandardAdapter(layoutResId, correctModule, items).apply {
            rv_list_score.adapter = this
            bindToRecyclerView(rv_list_score)
            if (correctModule == 2) {
                if (rv_list_score.itemDecorationCount != 0) {
                    rv_list_score.removeItemDecorationAt(0)
                }
            } else {
                if (rv_list_score.itemDecorationCount == 0)
                    rv_list_score.addItemDecoration(itemDeco)
            }
            setCustomItemChildClickListener { position, childPosition ->
                if (correctStatus == 1) {
                    val parentItem = items[position]
                    for (item in parentItem.list) {
                        item.isCheck = false
                    }
                    val childItem = parentItem.list[childPosition]
                    childItem.isCheck = true

                    parentItem.sort = childItem.sort
                    parentItem.score = childItem.score

                    notifyItemChanged(position)

                    if (isCorrectComplete()) {
                        results.clear()
                        var totalScores = 0.0
                        for (item in items) {
                            results.add(item.sort)
                            totalScores += item.score
                        }
                        val averageScore = totalScores / items.size
                        getScore(averageScore)
                    }
                }
            }
        }
    }

    private fun getScore(averageScore: Double) {
        if (correctModule == 1) {
            score = if (averageScore >= 85) {
                1
            } else if (averageScore >= 70) {
                2
            } else {
                3
            }
        } else {
            if (averageScore >= 95) {
                score = 1
            } else if (averageScore >= 90) {
                score = 2
            } else if (averageScore >= 85) {
                score = 3
            } else if (averageScore >= 80) {
                score = 4
            } else if (averageScore >= 75) {
                score = 5
            } else if (averageScore >= 70) {
                score = 6
            } else if (averageScore >= 65) {
                score = 7
            } else if (averageScore >= 60) {
                score = 8
            } else {
                score = 9
            }
        }
        tv_total_score.text = DataBeanManager.getResultStandardStr(score.toDouble(), correctModule)
    }

    /**
     * 判断批改是否完成
     */
    private fun isCorrectComplete(): Boolean {
        for (item in items) {
            if (item.score <= 0) {
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
        Thread {
            runOnUiThread {

                val userItem = userItems[posUser]
                correctStatus = userItem.status
                isDrawingSave = correctStatus == 1
                score = 0

                tv_take_time.text = if (userItem.takeTime > 0) "用时：${DateUtils.longToMinute(userItem.takeTime)}分钟" else ""

                items = getInitResultStandardItems()
                if (!userItem.question.isNullOrEmpty() && userItem.question.length < 20) {
                    val types = Gson().fromJson(userItem.question, object : TypeToken<MutableList<Int>>() {}.type) as MutableList<Int>
                    for (i in types.indices) {
                        val type = types[i]
                        if (i < items.size) {
                            val item = items[i]
                            for (childItem in item.list) {
                                if (childItem.sort == type) {
                                    childItem.isCheck = true
                                    item.score = childItem.score
                                }
                            }
                        }
                    }
                }

                if (correctList?.subType == 3) {
                    if (!userItem.studentUrl.isNullOrEmpty()) {
                        recordPath = userItem.studentUrl
                        speed=1f
                        isReadyRecorder=false
                        exoPlayer = ExoPlayer.Builder(this).build()
                        exoPlayer?.setMediaItem(MediaItem.fromUri(recordPath))
                        exoPlayer?.addListener(object : Player.Listener {
                            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                                when (playbackState) {
                                    Player.STATE_READY -> {
                                        isReadyRecorder=true
                                        val totalTime = exoPlayer?.duration!!.toInt()
                                        tv_end_time.text = DateUtils.secondToString(totalTime)
                                        progressBar.max = totalTime/1000
                                    }
                                    Player.STATE_ENDED -> {
                                        tv_start_time.text = "00:00"
                                        exoPlayer?.pause()
                                        exoPlayer?.seekTo(0)
                                        progressBar.progress = 0
                                        changeMediaView(false)
                                        timer?.cancel()
                                    }
                                }
                            }
                        })
                        exoPlayer?.prepare()
                        tv_start_time.text = "00:00"
                        changeMediaView(false)

                        showView(iv_file, ll_play, ll_progressbar,ll_speed)
                    } else {
                        disMissView(iv_file, ll_play, ll_progressbar,ll_speed)
                    }
                }

                when (correctStatus) {
                    1 -> {
                        currentImages = ToolUtils.getImages(userItem.studentUrl)
                        showView(ll_correct, tv_save)
                        disMissView(tv_share)

                        if (userItem.score > 0)
                            score = userItem.score.toInt()
                        tv_total_score.text = if (userItem.score > 0) DataBeanManager.getResultStandardStr(userItem.score, correctModule) else ""

                        setDisableTouchInput(false)
                        setPWEnabled(true)
                    }

                    2 -> {
                        currentImages = ToolUtils.getImages(userItem.submitUrl)
                        tv_total_score.text = DataBeanManager.getResultStandardStr(userItem.score, correctModule)
                        showView(ll_correct, tv_share)
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
                if (correctList?.subType == 3)
                    disMissView(tv_share)
                if (correctList?.subType == 10) {
                    ratingBar.rating = userItem.score.toFloat()
                    disMissView(ll_score)
                }

                mResultStandardAdapter?.setNewData(items)

                onChangeContent()

            }
        }.start()
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent() {
        if (currentImages.size == 0) {
            setContentImageClear()
            return
        }

        if (isExpand && posImage > getImageSize() - 2)
            posImage = getImageSize() - 2
        if (isExpand && posImage < 0)
            posImage = 0

        tv_page_total.text = "${getImageSize()}"
        tv_page_total_a.text = "${getImageSize()}"

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
        if (isExpand) {
            BitmapUtils.saveScreenShot(v_content_b, getPathMergeStr(posImage + 1))
        } else {
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
        if (correctList?.subType != 3)
            map["submitUrl"] = url
        map["status"] = 2
        map["score"] = score
        map["question"] = Gson().toJson(results)
        mPresenter.commitPaperStudent(map)

        if (correctList?.questionType!! < 0)
            mPresenter.changeQuestionType(mId, correctModule)
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

    private fun startTimer() {
        val periodTime = (1000/speed).toLong()
        Thread {
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        val currentTime=exoPlayer?.currentPosition!!.toInt()
                        progressBar.progress = currentTime/1000
                        tv_start_time.text = DateUtils.secondToString(currentTime)
                    }
                }
            }, 0, periodTime)
        }.start()
    }


    private fun release() {
        timer?.cancel()
        if (exoPlayer != null) {
            exoPlayer?.stop()
            exoPlayer?.release()
            exoPlayer = null
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