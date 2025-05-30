package com.bll.lnkteacher.ui.activity.exam

import android.content.Intent
import android.os.Bundle
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.exam.ExamList.ExamClassBean
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.presenter.ExamCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.ScoreRankActivity
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ScoreItemUtils
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_testpaper_analyse.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_answer
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_average_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_info_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_num_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_number_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse.tv_pop_score
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


class ExamAnalyseActivity:BaseDrawingActivity(),IContractView.IExamCorrectView {
    private val mPresenter= ExamCorrectPresenter(this,3)
    private var classPos=0
    private var posImage=0
    private var imageCount=0
    private var examBean: ExamList.ExamBean?=null
    private var classList= mutableListOf<ExamClassBean>()
    private var images= mutableListOf<String>()
    private var pops = mutableListOf<PopupBean>()
    private var users = mutableListOf<ExamClassUserList.ClassUserBean>()
    private var scoreIndex = 0//当前分数下标
    private var popClasss= mutableListOf<PopupBean>()

    override fun onExamClassUser(classUserList: ExamClassUserList) {
        totalAnalyseItems.clear()
        var totalScore=0.0
        for (userItem in classUserList.list) {
            if (userItem.studentUrl.isNullOrEmpty()) {
                userItem.status = 3
            } else {
                userItem.status = 1
            }
            if (userItem.teacherUrl.isNotEmpty()) {
                userItem.status = 2
            }
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
                            setAnalyseData(userItem,item,analyseItem)
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
        tv_number_score.text = users.size.toString()
        tv_pop_score.text = scoreIndex.toString()
        tv_info_score.text = "以上"
        tv_num_score.text = getScoreNum().toString()

        if (users.size > 0) {
            tv_average_score.text = ToolUtils.getFormatNum(totalScore/ users.size,"#.0")
        } else {
            tv_average_score.text = ""
        }

        if (correctModule>0){
            if (correctModule<3){
                mAnalyseAdapter?.setNewData(totalAnalyseItems)
            }
            else{
                mAnalyseMultiAdapter?.setNewData(totalAnalyseItems)
            }
            setChartView()
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamList.ExamBean
        classList=examBean!!.classList
        correctModule = examBean?.questionType!!
        scoreMode = examBean?.questionMode!!

        if (!examBean?.examUrl.isNullOrEmpty()) {
            images = ToolUtils.getImages(examBean?.examUrl)
        }
        if (!examBean?.answerUrl.isNullOrEmpty()) {
            answerImages = ToolUtils.getImages(examBean?.answerUrl)
        }

        for (item in classList) {
            popClasss.add(PopupBean(classList.indexOf(item),item.className,classList.indexOf(item)==classPos))
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
        disMissView(iv_tool,iv_catalog,iv_btn)
        showView(tv_class)
        setPageSetting("成绩统计")

        setPWEnabled(false)

        if (correctModule>0){
            setPageCustom("因材施教")
            showView(rv_list)
        }

        if (answerImages.size > 0){
            showView(tv_answer)
            tv_answer.setOnClickListener {
                ImageDialog(this,2,answerImages).builder()
            }
        }

        tv_class.text=classList[classPos].className
        tv_class.setOnClickListener {
            PopupRadioList(this,popClasss,tv_class,5).builder().setOnSelectListener{
                if (classPos != it.id) {
                    classPos = it.id
                    tv_class.text=classList[classPos].className
                    fetchClassUser()
                }
            }
        }

        tv_custom.setOnClickListener {
            val intent = Intent(this, ExamAnalyseTeachingActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("examBean", examBean)
            intent.putExtra("bundle", bundle)
            intent.putExtra("classPos", classPos)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_LEFT)
            customStartActivity(intent)
        }

        tv_custom_1.setOnClickListener {
            val intent = Intent(this, ScoreRankActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("examBean", examBean)
            intent.putExtra("bundle", bundle)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
            customStartActivity(intent)
        }

        tv_pop_score.setOnClickListener {
            PopupRadioList(this, pops, tv_pop_score, 170, 5).builder().setOnSelectListener {
                if (scoreMode == 1) {
                    if (it.id == 0) {
                        tv_pop_score.text = "60"
                        tv_info_score.text = "分以下"
                    } else {
                        tv_pop_score.text = "${it.id}"
                        tv_info_score.text = "分以上"
                    }
                }
                tv_num_score.text = getScoreNum().toString()
            }
        }

        initChartView()
        initRecyclerAnalyse()

        onChangeExpandView()

        if (images.size>0){
            imageCount=images.size
            onChangeContent()
        }
    }


    override fun onChangeExpandContent() {
        if (imageCount==1)
            return
        changeErasure()
        isExpand=!isExpand
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
    override fun onChangeContent(){
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

    /**
     * 数据分析赋值
     */
    private fun setAnalyseData(classUserBean: ExamClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem) {
        analyseItem.sort = scoreItem.sort
        analyseItem.sortStr=scoreItem.sortStr
        analyseItem.totalScore += scoreItem.score
        analyseItem.totalLabel+=scoreItem.label
        analyseItem.num += 1
        if (scoreItem.result == 0) {
            analyseItem.wrongNum += 1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.studentName, classUserBean.score))
        } else {
            analyseItem.rightNum += 1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.studentName, classUserBean.score))
        }
        analyseItem.averageScore = ToolUtils.getFormatNum(analyseItem.totalScore / analyseItem.num,"#.0").toDouble()
        analyseItem.scoreRate=analyseItem.totalScore/analyseItem.totalLabel
    }

    private fun fetchClassUser(){
        val map=HashMap<String,Any>()
        map["schoolExamJobId"]=examBean!!.id
        map["classId"]=classList[classPos].classId
        mPresenter.getExamClassUser(map)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().finishActivity(ExamAnalyseTeachingActivity::class.java.name)
    }

    override fun onRefreshData() {
        fetchClassUser()
    }

}