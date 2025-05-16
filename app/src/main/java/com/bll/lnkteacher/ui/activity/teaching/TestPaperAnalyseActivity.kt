package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ScoreItemUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_testpaper_analyse.ll_rate
import kotlinx.android.synthetic.main.ac_testpaper_analyse.ll_score_statistics
import kotlinx.android.synthetic.main.ac_testpaper_analyse.ll_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.ll_topic_statistics
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_answer
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_average_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_average_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_info_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_info_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_num_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_num_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_number_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_number_topic
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_pop_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_pop_topic
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import kotlinx.android.synthetic.main.common_title.tv_class
import kotlinx.android.synthetic.main.common_title.tv_custom
import kotlinx.android.synthetic.main.common_title.tv_custom_1


class TestPaperAnalyseActivity : BaseDrawingActivity(), IContractView.ITestPaperCorrectDetailsView {

    private var flag=0
    private var mPresenter = TestPaperCorrectDetailsPresenter(this, 3)
    private var classPos = 0
    private var posImage = 0
    private var imageCount = 0
    private var correctList: CorrectBean? = null
    private var classList= mutableListOf<TestPaperClassBean>()
    private var images = mutableListOf<String>()
    private var pops = mutableListOf<PopupBean>()
    private var users = mutableListOf<TestPaperClassUserList.ClassUserBean>()
    private var scoreIndex = 0//当前分数下标
    private var popClasss= mutableListOf<PopupBean>()

    override fun onClassPapers(bean: TestPaperClassUserList) {
        users.clear()
        totalAnalyseItems.clear()

        var totalScore = 0.0
        for (userItem in bean.taskList) {
            if (!userItem.question.isNullOrEmpty() && userItem.status == 2 && correctModule > 0) {
                currentScores = ScoreItemUtils.jsonListToModuleList(correctModule,ScoreItemUtils.questionToList(userItem.question))
                for (item in currentScores) {
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
                            setAnalyseData(userItem,item,analyseItem)

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
                            for (childItem in item.childScores) {
                                val index = item.childScores.indexOf(childItem)
                                val childExamAnalyseItem = analyseItem.childAnalyses[index]
                                setAnalyseData(userItem, childItem, childExamAnalyseItem)
                            }
                            setAnalyseData(userItem,item,analyseItem)
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
        if (scoreMode==1){
            if (users.size > 0) {
                tv_average_score.text = ToolUtils.getFormatNum(totalScore / users.size,"#.0")
            } else {
                tv_average_score.text = ""
            }
            tv_number_score.text = users.size.toString()
            tv_pop_score.text = scoreIndex.toString()
            tv_num_score.text = getScoreNum().toString()
        }
        else{
            if (users.size > 0) {
                tv_average_topic.text = ToolUtils.getFormatNum(totalScore / users.size,"#.0")
            } else {
                tv_average_topic.text = ""
            }
            tv_number_topic.text = users.size.toString()
            tv_pop_topic.text = scoreIndex.toString()
            tv_num_topic.text = getScoreNum().toString()
        }

        if (correctModule > 0) {
            if (correctModule < 3) {
                mAnalyseAdapter?.setNewData(totalAnalyseItems)
            } else {
                mAnalyseMultiAdapter?.setNewData(totalAnalyseItems)
            }
            setChartView()
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos = Constants.SCREEN_LEFT
        flag=intent.flags
        correctList = intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        classList=correctList!!.examList
        correctModule = correctList?.questionType!!
        scoreMode = correctList?.questionMode!!

        for (item in classList) {
            popClasss.add(PopupBean(classList.indexOf(item),item.name,classList.indexOf(item)==classPos))
        }

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
        } else {
            for (score in intArrayOf(0, 1, 5, 10, 20)) {
                pops.add(PopupBean(score, "${score}题", score==1))
            }
        }

        fetchClassUser()
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        disMissView(iv_tool, iv_catalog, iv_btn)
        showView(tv_class)

        setPWEnabled(false)

        if (flag==0){
            setPageCustom("层群创建")
            setPageSetting("成绩统计")
        }
        if (scoreMode!=1){
            showView(ll_topic_statistics)
            disMissView(ll_score_statistics)
        }

        if (answerImages.size > 0){
            showView(tv_answer)
            tv_answer.setOnClickListener {
                ImageDialog(this,2,answerImages).builder()
            }
        }

        if (correctModule == 0){
            disMissView(ll_topic,ll_rate)
        }

        tv_class.text=classList[classPos].name
        tv_class.setOnClickListener {
            PopupRadioList(this,popClasss,tv_class,5).builder().setOnSelectListener{
                if (classPos != it.id) {
                    classPos = it.id
                    tv_class.text=classList[classPos].name
                    fetchClassUser()
                }
            }
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

        tv_custom_1.setOnClickListener {
            val intent = Intent(this, ScoreRankActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("paperCorrect", correctList)
            intent.putExtra("bundle", bundle)
            intent.flags = 0
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
            customStartActivity(intent)
        }

        if (scoreMode==1){
            tv_pop_score.setOnClickListener {
                PopupRadioList(this, pops, tv_pop_score, 170, 5).builder().setOnSelectListener {
                    scoreIndex = it.id
                    if (scoreIndex == 0) {
                        tv_pop_score.text = "60"
                        tv_info_score.text = "以下"
                    } else {
                        tv_pop_score.text = "$scoreIndex"
                        tv_info_score.text = "以上"
                    }
                    tv_num_score.text ="${getScoreNum()}"
                }
            }

        }
        else{
            tv_pop_topic.setOnClickListener {
                PopupRadioList(this, pops, tv_pop_topic, 170, 5).builder().setOnSelectListener {
                    scoreIndex = it.id
                    tv_pop_topic.text = "$scoreIndex"
                    if (scoreIndex == 0) {
                        tv_info_topic.text = "全错"
                    } else {
                        tv_info_topic.text = "以上"
                    }
                    tv_num_score.text = "${getScoreNum()}"
                }
            }
        }


        onChangeExpandView()

        initChartView()
        initRecyclerAnalyse()

        if (images.size > 0) {
            imageCount = images.size
            onChangeContent()
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
                    if (item.score.toInt() == 0) {
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
        if (imageCount==1)
            return
        changeErasure()
        isExpand = !isExpand
        onChangeExpandView()
        onChangeContent()
    }


    override fun onPageUp() {
        if (posImage>0){
            posImage-=if (isExpand)2 else 1
            onChangeContent()
        }
    }

    override fun onPageDown() {
        val count=if (isExpand) imageCount-2 else imageCount-1
        if (posImage<count){
            posImage+=if (isExpand)2 else 1
            onChangeContent()
        }
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent() {
        if (isExpand&&posImage>imageCount-2)
            posImage=imageCount-2
        if (isExpand&&posImage<0)
            posImage=0

        tv_page_total_a.text = "$imageCount"
        tv_page_total.text = "$imageCount"

        if (isExpand){
            GlideUtils.setImageUrl(this, images[posImage],v_content_a)
            GlideUtils.setImageUrl(this, images[posImage+1],v_content_b)
            tv_page.text="${posImage+1}"
            tv_page_a.text="${posImage+1+1}"
        }
        else{
            GlideUtils.setImageUrl(this, images[posImage],v_content_b)
            tv_page.text="${posImage+1}"
        }
    }

    /**
     * 获取班级学生列表
     */
    private fun fetchClassUser(){
        mPresenter.getPaperClassPapers(correctList?.id!!, classList[classPos].classId)
    }

    /**
     * 数据分析赋值
     */
    private fun setAnalyseData(classUserBean: TestPaperClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem) {
        analyseItem.sort = scoreItem.sort
        analyseItem.sortStr=scoreItem.sortStr
        analyseItem.totalScore += scoreItem.score
        analyseItem.totalLabel+=scoreItem.label
        analyseItem.num += 1
        if (scoreItem.result == 0) {
            analyseItem.wrongNum += 1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        } else {
            analyseItem.rightNum += 1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        if (scoreMode == 1) {
            analyseItem.averageScore =ToolUtils.getFormatNum(analyseItem.totalScore / analyseItem.num,"#.0").toDouble()
            analyseItem.scoreRate=analyseItem.totalScore/analyseItem.totalLabel
        } else {
            analyseItem.averageScore = analyseItem.rightNum.toDouble() / analyseItem.num
            analyseItem.scoreRate= (analyseItem.rightNum / analyseItem.num).toDouble()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().finishActivity(TestPaperAnalyseTeachingActivity::class.java.name)
    }

    override fun onRefreshData() {
        fetchClassUser()
    }
}