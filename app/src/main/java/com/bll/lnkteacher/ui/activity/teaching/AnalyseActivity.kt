package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.AnalyseUserDetailsDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamAnalyseAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseMultiAdapter
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DecimalFormat

class AnalyseActivity : BaseDrawingActivity(), IContractView.ITestPaperCorrectDetailsView {

    private var mPresenter = TestPaperCorrectDetailsPresenter(this, 3)
    private var type = 0
    private var classPos = 0
    private var posImage = 0
    private var imageCount = 0
    private var scoreMode = 0//1赋分，2对错
    private var correctList: CorrectBean? = null
    private var images = mutableListOf<String>()
    private var pops = mutableListOf<PopupBean>()
    private var totalAnalyseItems = mutableListOf<AnalyseItem>() //题目集合
    private var mAnalyseAdapter: ExamAnalyseAdapter? = null
    private var mAnalyseMultiAdapter: ExamAnalyseMultiAdapter? = null
    private var users = mutableListOf<TestPaperClassUserList.ClassUserBean>()
    private var scoreIndex = 0//当前分数下标

    override fun onClassPapers(bean: TestPaperClassUserList) {
        users.clear()
        totalAnalyseItems.clear()

        var totalScore = 0
        for (userItem in bean.list) {
            correctModule = userItem.questionType
            if (!userItem.question.isNullOrEmpty() && userItem.status == 2 && correctModule > 0) {
                currentScores = jsonToList(userItem.question) as MutableList<ScoreItem>
                for (item in currentScores) {
                    val currentScore = MethodManager.getScore(item.score)
                    if (correctModule < 3) {
                        if (totalAnalyseItems.size < currentScores.size) {
                            val analyseItem = AnalyseItem()
                            setAnalyseData(userItem, item, analyseItem)
                            totalAnalyseItems.add(analyseItem)
                        } else {
                            val examAnalyseItem = totalAnalyseItems[item.sort - 1]
                            setAnalyseData(userItem, item, examAnalyseItem)
                        }
                    } else {
                        if (totalAnalyseItems.size < currentScores.size) {
                            val analyseItem = AnalyseItem()
                            analyseItem.sort = item.sort
                            analyseItem.totalScore += currentScore
                            analyseItem.num += 1
                            analyseItem.averageScore = analyseItem.totalScore / analyseItem.num
                            val childAnalyseItems = mutableListOf<AnalyseItem>()
                            for (childItem in item.childScores) {
                                val childAnalyseItem = AnalyseItem()
                                setAnalyseData(userItem, childItem, childAnalyseItem)
                                childAnalyseItems.add(childAnalyseItem)
                            }
                            analyseItem.childAnalyses = childAnalyseItems
                            totalAnalyseItems.add(analyseItem)
                        } else {
                            val analyseItem = totalAnalyseItems[item.sort - 1]
                            analyseItem.totalScore += currentScore
                            analyseItem.num += 1
                            analyseItem.averageScore = analyseItem.totalScore / analyseItem.num
                            for (childItem in item.childScores) {
                                val index = item.childScores.indexOf(childItem)
                                val childExamAnalyseItem = analyseItem.childAnalyses[index]
                                setAnalyseData(userItem, childItem, childExamAnalyseItem)
                            }
                        }
                    }
                }
            }
            //已批改
            if (userItem.status == 2) {
                users.add(userItem)
                totalScore += userItem.score
            }
        }

        scoreIndex = pops[1].id
        tv_correct_number.text = users.size.toString()
        tv_score_pop.text = scoreIndex.toString()
        tv_score_info.text = "以上"
        tv_num.text = getScoreNum().toString()

        if (users.size > 0) {
            tv_average_score.text = getAverageNum(totalScore.toDouble() / users.size)
        } else {
            tv_average_score.text = ""
        }

        if (correctModule > 0) {
            if (correctModule < 3) {
                mAnalyseAdapter?.setNewData(totalAnalyseItems)
            } else {
                mAnalyseMultiAdapter?.setNewData(totalAnalyseItems)
            }
        }
    }

    override fun onCorrectSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT

        correctList = intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        correctModule = correctList?.questionType!!
        scoreMode = correctList?.questionMode!!
        type = correctList?.taskType!!

        for (item in correctList?.examList!!) {
            mExamClassGroups.add(ClassGroup().apply {
                classId = item.classId
                classGroupId = item.classGroupId
                name = item.name
            })
        }
        mExamClassGroups[classPos].isCheck = true

        if (!correctList?.examUrl.isNullOrEmpty()) {
            images = ToolUtils.getImages(correctList?.examUrl)
        }
        if (!correctList?.answerUrl.isNullOrEmpty()) {
            answerImages = ToolUtils.getImages(correctList?.answerUrl)
        }

        if (scoreMode == 1) {
            for (score in intArrayOf(0, 60, 70, 80, 90, 100)) {
                pops.add(PopupBean(score, "${score}分", false))
            }
        } else {
            for (score in intArrayOf(0, 1, 5, 10, 20)) {
                pops.add(PopupBean(score, "${score}题", false))
            }
        }

        mPresenter.getClassPapers(correctList?.examList!![classPos].examChangeId)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        disMissView(iv_tool, iv_catalog, iv_btn)
        setPageSetting("成绩统计")
        if (type == 2) {
            setPageCustom("因材施教")
        }
        if (answerImages.size > 0)
            showView(tv_answer)

        if (correctModule == 0)
            disMissView(ll_topic)

        tv_average_info.text=if (scoreMode==1) "平均分" else "平均数"

        tv_answer.setOnClickListener {
            ImageDialog(this,2, answerImages).builder()
        }

        tv_custom.setOnClickListener {
            val intent = Intent(this, AnalyseTeachingActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("paperCorrect", correctList)
            intent.putExtra("bundle", bundle)
            intent.putExtra("classPos", classPos)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_LEFT)
            customStartActivity(intent)
        }

        tv_setting.setOnClickListener {
            val intent = Intent(this, ScoreRankActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("paperCorrect", correctList)
            intent.putExtra("bundle", bundle)
            intent.flags = 0
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
            customStartActivity(intent)
        }

        tv_score_pop.setOnClickListener {
            PopupRadioList(this, pops, tv_score_pop, 5).builder().setOnSelectListener {
                scoreIndex = it.id
                if (scoreMode==1){
                    if (scoreIndex == 0 ) {
                        tv_score_pop.text = "60"
                        tv_score_info.text = "以下"
                    } else {
                        tv_score_pop.text = "$scoreIndex"
                        tv_score_info.text = "以上"
                    }
                }
                else{
                    tv_score_pop.text = "$scoreIndex"
                    if (scoreIndex == 0 ) {
                        tv_score_info.text = "全错"
                    } else {
                        tv_score_info.text = "以上"
                    }
                }
                tv_num.text = getScoreNum().toString()
            }
        }

        iv_score_up.setOnClickListener {
            rv_list.scrollBy(0, -DP2PX.dip2px(this, 100f))
        }

        iv_score_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this, 100f))
        }

        onChangeExpandView()

        initRecyclerView()
        mClassAdapter?.setNewData(mExamClassGroups)

        if (images.size > 0) {
            imageCount = images.size
            onChangeContent()
        }
    }

    override fun onClassClickListener(view: View, position: Int) {
        if (classPos != position) {
            classPos = position
            mPresenter.getClassPapers(correctList?.examList!![classPos].examChangeId)
        }
    }

    private fun initRecyclerView() {
        if (correctModule < 3) {
            rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
            mAnalyseAdapter = ExamAnalyseAdapter(R.layout.item_exam_analyse_score, scoreMode, correctModule, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setOnItemChildClickListener { adapter, view, position ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].wrongStudents
                        AnalyseUserDetailsDialog(this@AnalyseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceGridItemDeco(2,DP2PX.dip2px(this,15f)))
        } else {
            rv_list.layoutManager = LinearLayoutManager(this)
            mAnalyseMultiAdapter = ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score, scoreMode, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setCustomItemChildClickListener { position, view, childPosition ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].childAnalyses[childPosition].wrongStudents
                        AnalyseUserDetailsDialog(this@AnalyseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this,15f)))
        }
    }

    /**
     * 获取当前成绩人数
     */
    private fun getScoreNum(): Int {
        var num = 0
        for (item in users) {
            if (scoreMode == 1) {
                if (scoreIndex == 0) {
                    if (item.score < 60) {
                        num += 1
                    }
                } else {
                    if (item.score >= scoreIndex) {
                        num += 1
                    }
                }
            } else {
                if (scoreIndex == 0) {
                    if (item.score == 0) {
                        num += 1
                    }
                } else {
                    if (item.score >= scoreIndex) {
                        num += 1
                    }
                }
            }
        }
        return num
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageUp() {
        if (isExpand) {
            if (posImage > 1) {
                posImage -= 2
            } else if (posImage == 1) {
                posImage = 0
            }
        } else {
            if (posImage > 0) {
                posImage -= 1
            }
        }
        onChangeContent()
    }

    override fun onPageDown() {
        if (isExpand) {
            if (posImage < imageCount - 2) {
                posImage += 2
            } else if (posImage == imageCount - 2) {
                posImage = imageCount - 1
            }
        } else {
            if (posImage < imageCount - 1) {
                posImage += 1
            }
        }
        onChangeContent()
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent() {
        tv_page_total_a.text = "$imageCount"
        tv_page_total.text = "$imageCount"

        if (isExpand) {
            elik_a?.setPWEnabled(true, true)
            GlideUtils.setImageUrl(this, images[posImage], v_content_a)
            val drawPath = getPathDrawStr(posImage + 1)
            elik_a?.setLoadFilePath(drawPath, true)

            if (posImage + 1 < imageCount) {
                elik_b?.setPWEnabled(true, true)
                GlideUtils.setImageUrl(this, images[posImage + 1], v_content_b)
                val drawPath_b = getPathDrawStr(posImage + 1 + 1)
                elik_b?.setLoadFilePath(drawPath_b, true)
            } else {
                elik_b?.setPWEnabled(false, false)
                v_content_b?.setImageResource(0)
            }
            tv_page.text = "${posImage + 1}"
            tv_page_a.text = if (posImage + 1 < imageCount) "${posImage + 1 + 1}" else ""
        } else {
            elik_b?.setPWEnabled(true, true)
            GlideUtils.setImageUrl(this, images[posImage], v_content_b)
            val drawPath = getPathDrawStr(posImage + 1)
            elik_b?.setLoadFilePath(drawPath, true)
            tv_page.text = "${posImage + 1}"
        }
    }


    /**
     * 获取平均数
     */
    private fun getAverageNum(number: Double): String {
        if (number == 0.0) {
            return "0"
        }
        val decimalFormat = DecimalFormat("#.0")
        return decimalFormat.format(number)
    }

    /**
     * 文件路径
     */
    private fun getPath(): String {
        return FileAddress().getPathTestPaperDrawing(correctList!!.id)
    }


    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int): String {
        return getPath() + "/draw${index}.tch"//手绘地址
    }

    /**
     * 数据分析赋值
     */
    private fun setAnalyseData(classUserBean: TestPaperClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem) {
        analyseItem.sort = scoreItem.sort
        analyseItem.totalScore += MethodManager.getScore(scoreItem.score)
        analyseItem.num += 1
        if (scoreItem.result == 0) {
            analyseItem.wrongNum += 1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        } else {
            analyseItem.rightNum += 1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        if (scoreMode == 1) {
            analyseItem.averageScore = analyseItem.totalScore / analyseItem.num
        } else {
            analyseItem.averageScore = analyseItem.rightNum.toDouble() / analyseItem.num
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().finishActivity(AnalyseTeachingActivity::class.java.name)
    }

}