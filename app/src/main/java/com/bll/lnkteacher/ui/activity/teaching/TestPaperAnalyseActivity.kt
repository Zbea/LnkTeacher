package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.graphics.Color
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.ac_testpaper_analyse.barChart
import kotlinx.android.synthetic.main.ac_testpaper_analyse.iv_score_down
import kotlinx.android.synthetic.main.ac_testpaper_analyse.iv_score_up
import kotlinx.android.synthetic.main.ac_testpaper_analyse.ll_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_answer
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_average_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_correct_number
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_num
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_score_info
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_score_label
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_score_pop
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import kotlinx.android.synthetic.main.common_title.tv_custom
import kotlinx.android.synthetic.main.common_title.tv_setting


class TestPaperAnalyseActivity : BaseDrawingActivity(), IContractView.ITestPaperCorrectDetailsView {

    private var mPresenter = TestPaperCorrectDetailsPresenter(this, 3)
    private var classPos = 0
    private var posImage = 0
    private var imageCount = 0
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
        for (userItem in bean.taskList) {
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
                            val examAnalyseItem = totalAnalyseItems[item.sort]
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
                            val analyseItem = totalAnalyseItems[item.sort]
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
            tv_average_score.text = ToolUtils.getFormatNum(totalScore.toDouble() / users.size,"#.0")
        } else {
            tv_average_score.text = ""
        }

        if (correctModule > 0) {
            val barEntries= mutableListOf<BarEntry>()
            val topicStrs= mutableListOf<String>()
            if (correctModule < 3) {
                mAnalyseAdapter?.setNewData(totalAnalyseItems)
                for (i in 0 until totalAnalyseItems.size){
                    topicStrs.add("${i+1}")
                    barEntries.add(BarEntry(i.toFloat(), totalAnalyseItems[i].scoreRate.toFloat()))
                }
            } else {
                mAnalyseMultiAdapter?.setNewData(totalAnalyseItems)
                var count=0
                for (item in totalAnalyseItems){
                    val childItems=item.childAnalyses
                    for (i in 0 until childItems.size){
                        topicStrs.add("${count+i+1}")
                        barEntries.add(BarEntry((count+i).toFloat(), childItems[i].scoreRate.toFloat()))
                    }
                    count+=childItems.size
                }
            }

            val barDataSet=BarDataSet(barEntries,"")
            val barDataSets= mutableListOf<IBarDataSet>()
            barDataSets.add(barDataSet)
            val barData=BarData(barDataSets)

            val xAxis=barChart.xAxis
            xAxis.valueFormatter=IndexAxisValueFormatter(topicStrs)
            xAxis.labelCount=topicStrs.size

            barChart.data=barData
            barChart.invalidate()
        }


    }

    override fun onCorrectSuccess() {
    }

    override fun onCompleteSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT

        correctList = intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        correctModule = correctList?.questionType!!
        scoreMode = correctList?.questionMode!!

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
                pops.add(PopupBean(score, "${score}分", score==60))
            }
        }
        fetchClassUser()
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        disMissView(iv_tool, iv_catalog, iv_btn)
        setPageSetting("成绩统计")
        setPageCustom("因材施教")

        if (answerImages.size > 0)
            showView(tv_answer)

        if (correctModule == 0)
            disMissView(ll_topic)

        tv_score_label.text = if (scoreMode == 1) "赋分统计数据" else "对错统计数据"

        tv_answer.setOnClickListener {
            ImageDialog(this, 2, answerImages).builder()
        }

        tv_custom.setOnClickListener {
            val intent = Intent(this, TestPaperAnalyseTeachingActivity::class.java)
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
            PopupRadioList(this, pops, tv_score_pop, 170, 5).builder().setOnSelectListener {
                scoreIndex = it.id
                if (scoreMode == 1) {
                    if (scoreIndex == 0) {
                        tv_score_pop.text = "60"
                        tv_score_info.text = "以下"
                    } else {
                        tv_score_pop.text = "$scoreIndex"
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

        initChartView()
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
            fetchClassUser()
        }
    }

    private fun initChartView() {
        barChart.description.isEnabled=false
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.setTouchEnabled(false)
        barChart.setPinchZoom(false)

        //x轴设置
        val xAxis=barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.axisLineColor=Color.parseColor("#000000")
        xAxis.textColor=Color.parseColor("#000000")
        xAxis.textSize=14f
        // Y轴左边
        val  leftAxis = barChart.axisLeft
        leftAxis.setAxisMinValue(0f) // 设置最小值
        leftAxis.setDrawGridLines(false)
        leftAxis.axisLineColor=Color.parseColor("#000000")
        //y轴右边设置
        val rightAxis = barChart.axisRight
        rightAxis.setAxisMinValue(0f) // 设置最小值
        rightAxis.setDrawGridLines(false) // 不绘制网格线
        rightAxis.isEnabled=false
    }

    private fun initRecyclerView() {
        if (correctModule < 3) {
            rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
            mAnalyseAdapter = ExamAnalyseAdapter(R.layout.item_exam_analyse_score, correctModule, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setOnItemChildClickListener { adapter, view, position ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].wrongStudents
                        if (students.size>0)
                            AnalyseUserDetailsDialog(this@TestPaperAnalyseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(this, 15f)))
        } else {
            rv_list.layoutManager = LinearLayoutManager(this)
            mAnalyseMultiAdapter = ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setCustomItemChildClickListener { position, view, childPosition ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].childAnalyses[childPosition].wrongStudents
                        if (students.size>0)
                            AnalyseUserDetailsDialog(this@TestPaperAnalyseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this, 15f)))
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
     * 获取班级学生列表
     */
    private fun fetchClassUser(){
        mPresenter.getPaperClassPapers(correctList?.id!!, correctList?.examList!![classPos].classId)
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
        analyseItem.totalLabel+=scoreItem.label
        analyseItem.num += 1
        if (scoreItem.result == 0) {
            analyseItem.wrongNum += 1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        } else {
            analyseItem.rightNum += 1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        analyseItem.averageScore = analyseItem.totalScore / analyseItem.num
        analyseItem.scoreRate=analyseItem.totalScore/analyseItem.totalLabel
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().finishActivity(TestPaperAnalyseTeachingActivity::class.java.name)
    }

    override fun onRefreshData() {
        fetchClassUser()
    }
}