package com.bll.lnkteacher.ui.activity.exam

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ImageDialog
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
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ScoreItemUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_testpaper_correct.iv_expand_arrow
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
import java.util.stream.Collectors

class ExamCorrectActivity : BaseDrawingActivity(), IContractView.IExamCorrectView, IFileUploadView {

    private var examBean: ExamCorrectList.ExamCorrectBean? = null
    private val mUploadPresenter = FileUploadPresenter(this, 3)
    private val mPresenter = ExamCorrectPresenter(this, 3)
    private var userItems = mutableListOf<ExamClassUserList.ClassUserBean>()

    private var mAdapter: ExamCorrectUserAdapter? = null
    private var url = ""//上个学生提交地址
    private var posImage = 0//当前图片下标
    private var posUser = 0//当前学生下标
    private var currentImages = mutableListOf<String>()
    private var tokenStr = ""
    private var initScores = mutableListOf<ScoreItem>()//初始题目分数
    private var isTopicExpend = false

    override fun onToken(token: String) {
        tokenStr = token
    }

    override fun onExamClassUser(classUserList: ExamClassUserList) {
        userItems.clear()
        for (item in classUserList.list) {
            if (item.studentUrl.isNullOrEmpty()) {
                item.status = 3
            } else {
                item.status = 1
            }
            if (item.teacherUrl.isNotEmpty()) {
                item.status = 2
            }
            userItems.add(item)
        }
        if (userItems.size > 0) {
            userItems[posUser].isCheck = true

            mAdapter?.setNewData(userItems)
            Handler().post {
                setContentView()
            }
        }
    }

    override fun onCorrectSuccess() {
        showToast(userItems[posUser].studentName + getString(R.string.teaching_correct_success))
        correctStatus = 2
        userItems[posUser].score = tv_total_score.text.toString().toDouble()
        userItems[posUser].teacherUrl = url
        userItems[posUser].status = 2
        userItems[posUser].question = Gson().toJson(initScores)
        userItems[posUser].currentScores = currentScores.stream().collect(Collectors.toList())
        mAdapter?.notifyItemChanged(posUser)
        disMissView(tv_save)
        setDisableTouchInput(true)
        FileUtils.deleteFile(File(getPath()))
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT
        examBean = intent.getBundleExtra("bundle")?.get("examBean") as ExamCorrectList.ExamCorrectBean
        correctModule = examBean?.questionType!!
        scoreMode = examBean?.questionMode!!
        if (!examBean?.answerUrl.isNullOrEmpty())
            answerImages = examBean?.answerUrl?.split(",") as MutableList<String>

        fetchData()
    }

    override fun initView() {
        setPageTitle("考卷批改  ${examBean?.examName}  ${examBean?.className}")
        disMissView(iv_catalog, iv_btn)

        if (answerImages.size > 0) {
            showView(tv_answer)
            tv_answer.setOnClickListener {
                ImageDialog(this, 2, answerImages).builder()
            }
        }

        tv_total_score.setOnClickListener {
            val item = userItems[posUser]
            if (item.status == 1) {
                NumberDialog(this, getCurrentScreenPos(), "请输入总分").builder().setDialogClickListener {
                    tv_total_score.text = it.toString()
                }
            }
        }

        iv_expand_arrow.setOnClickListener {
            if (isTopicExpend) {
                isTopicExpend = false
                setTopicExpend(false)
            } else {
                isTopicExpend = true
                setTopicExpend(true)
            }
        }

        tv_save.setOnClickListener {
            hideKeyboard()
            if (correctStatus == 1 && tv_total_score.text.toString().isNotEmpty()) {
                showLoading()
                //将赋值数据初始化给原数据
                initScores = ScoreItemUtils.updateInitListData(initScores, currentScores, correctModule)
                if (isTopicExpend) {
                    isTopicExpend = false
                    setTopicExpend(false)
                }
                //没有手写，直接提交
                if (!FileUtils.isExistContent(getPathDraw())) {
                    url = userItems[posUser].studentUrl
                    commit()
                } else {
                    commitPaper()
                }
            }
        }

        initRecyclerView()
        initRecyclerViewScore()

        onChangeExpandView()
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        mAdapter = ExamCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(7, 30))
            setOnItemClickListener { adapter, view, position ->
                if (position == posUser)
                    return@setOnItemClickListener
                val oldItem = userItems[posUser]
                oldItem.isCheck = false
                oldItem.currentScores = currentScores.stream().collect(Collectors.toList())
                oldItem.score = tv_total_score.text.toString().toDouble()
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

    override fun onChangeExpandContent() {
        if (getImageSize() == 1)
            return
        if (posImage >= getImageSize() - 1 && getImageSize() > 1)
            posImage = getImageSize() - 2
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

                when (correctStatus) {
                    1 -> {
                        initScores = ScoreItemUtils.questionToList(examBean?.question!!)
                        currentScores = if (userItem.currentScores.isNotEmpty()) {
                            userItem.currentScores.stream().collect(Collectors.toList())
                        } else {
                            ScoreItemUtils.jsonListToModuleList(correctModule, ScoreItemUtils.questionToList(examBean?.question!!))
                        }
                        currentImages = ToolUtils.getImages(userItem.studentUrl)
                        tv_total_score.text = ""
                        showView(ll_score, ll_score_topic, tv_save)

                        setDisableTouchInput(false)
                        setPWEnabled(true)
                    }

                    2 -> {
                        currentScores = if (userItem.currentScores.isNotEmpty()) {
                            userItem.currentScores.stream().collect(Collectors.toList())
                        } else {
                            ScoreItemUtils.jsonListToModuleList(correctModule, ScoreItemUtils.questionToList(userItem.question))
                        }
                        currentImages = ToolUtils.getImages(userItem.teacherUrl)
                        tv_total_score.text = userItem.score.toString()
                        showView(ll_score, ll_score_topic)
                        disMissView(tv_save)

                        setDisableTouchInput(true)
                        setPWEnabled(false)
                    }

                    3 -> {
                        currentScores.clear()
                        currentImages = mutableListOf()
                        disMissView(ll_score, ll_score_topic)

                        setDisableTouchInput(true)
                        setPWEnabled(false)
                    }
                }

                setRecyclerViewScoreAdapter()
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
     * 每份多少张考卷
     */
    private fun getImageSize(): Int {
        if (currentImages.isEmpty())
            return 0
        return currentImages.size
    }

    /**
     * 上传图片
     */
    private fun commitPaper() {
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
     * 提交考卷
     */
    private fun commit() {
        val map = HashMap<String, Any>()
        map["id"] = userItems[posUser].id
        map["schoolExamJobId"] = userItems[posUser].schoolExamJobId
        map["score"] = tv_total_score.text.toString().toDouble()
        map["teacherUrl"] = url
        map["classId"] = userItems[posUser].classId
        map["status"] = 2
        map["question"] = Gson().toJson(initScores)
        mPresenter.onExamCorrectComplete(map)
    }

    /**
     * 文件路径
     */
    private fun getPath(): String {
        return FileAddress().getPathExam(examBean?.id, examBean?.classId, userItems[posUser].userId)
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

    override fun fetchData() {
        mUploadPresenter.getToken(false)

        val map = HashMap<String, Any>()
        map["schoolExamJobId"] = examBean?.schoolExamJobId!!
        map["classId"] = examBean?.classId!!
        mPresenter.getExamClassUser(map)
    }

    override fun onRefreshData() {
        fetchData()
    }

}