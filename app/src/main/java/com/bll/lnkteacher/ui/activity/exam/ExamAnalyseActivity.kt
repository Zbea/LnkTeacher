package com.bll.lnkteacher.ui.activity.exam

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.ScoreRankActivity
import com.bll.lnkteacher.ui.adapter.ExamAnalyseAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseMultiAdapter
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_drawing_page_number.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DecimalFormat

class ExamAnalyseActivity:BaseDrawingActivity(),IContractView.IExamListView {
    private var mPresenter=ExamListPresenter(this,3)
    private var classId=0
    private var posImage=0
    private var imageCount=0
    private var examBean: ExamList.ExamBean?=null
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()
    private var scoreItems= mutableListOf<ScoreItem>()
    private var popScore= mutableListOf<PopupBean>()
    private var totalAnalyseItems= mutableListOf<AnalyseItem>() //题目集合

    private var mAnalyseAdapter:ExamAnalyseAdapter?=null
    private var mAnalyseMultiAdapter:ExamAnalyseMultiAdapter?=null

    override fun onList(list: ExamList) {
    }
    override fun onExamClassUser(classUserList: ExamClassUserList) {
        scoreItems.clear()
        popScore.clear()
        totalAnalyseItems.clear()

        for (userItem in classUserList.list) {
            if (userItem.studentUrl.isNullOrEmpty()) {
                userItem.status = 3
            } else {
                userItem.status = 1
            }
            if (userItem.teacherUrl.isNotEmpty()) {
                userItem.status = 2
            }
            correctModule = userItem.questionType
            if (!userItem.question.isNullOrEmpty()) {
                if (!userItem.question.isNullOrEmpty() && userItem.status == 2 && correctModule > 0) {
                    currentScores = jsonToList(userItem.question) as MutableList<ScoreItem>
                }
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
                            val examAnalyseItem = totalAnalyseItems[item.sort - 1]
                            examAnalyseItem.totalScore += currentScore
                            examAnalyseItem.num += 1
                            examAnalyseItem.averageScore = examAnalyseItem.totalScore / examAnalyseItem.num
                            for (childItem in item.childScores) {
                                val index = item.childScores.indexOf(childItem)
                                val childExamAnalyseItem = examAnalyseItem.childAnalyses[index]
                                setAnalyseData(userItem, childItem, childExamAnalyseItem)
                            }
                        }
                    }
                }
            }
        }


        if (correctModule>0){
            if (correctModule<3){
                mAnalyseAdapter?.setNewData(totalAnalyseItems)
            }
            else{
                mAnalyseMultiAdapter?.setNewData(totalAnalyseItems)
            }
        }
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamList.ExamBean
        images=examBean?.examUrl!!.split(",").toMutableList()

        for (item in examBean?.classList!!){
            popClasss.add(PopupBean(item.classId,item.className,false))
        }
        classId=popClasss[0].id

        val map1=HashMap<String,Any>()
        map1["schoolExamJobId"]=examBean!!.id
        map1["classId"]=classId
        mPresenter.getExamClassUser(map1)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        showView(tv_class)
        disMissView(iv_tool,iv_catalog,ll_correct,iv_btn)
        setPageSetting("成绩统计")

        if (correctModule>0){
            setPageCustom("因材施教")
            showView(rv_list)
        }

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true

        tv_setting.setOnClickListener {
            val intent= Intent(this, ScoreRankActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("examBean",examBean)
            intent.putExtra("bundle",bundle)
            intent.flags=Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
            customStartActivity(intent)
        }

        tv_class.setOnClickListener {
            PopupRadioList(this, popClasss, tv_class ,5).builder()
                .setOnSelectListener { item ->
                    if (classId!=item.id){
                        classId=item.id
                        tv_class.text = item.name
                        val map=HashMap<String,Any>()
                        map["schoolExamJobId"]=examBean!!.id
                        map["classId"]=classId
                        mPresenter.getExamClassUser(map)
                    }
                }
        }

        tv_score_pop.setOnClickListener {
            PopupRadioList(this,popScore,tv_score_pop,5).builder().setOnSelectListener{
                if (it.name=="0"){
                    tv_score_pop.text="60分"
                    tv_score_info.text="以下"
                }
                else{
                    tv_score_pop.text=it.name+"分"
                    tv_score_info.text="以上"
                }
                tv_num.text=it.id.toString()
            }
        }

        initRecyclerView()

        onChangeExpandView()

        if (images.size>0){
            imageCount=images.size
            onChangeContent()
        }
    }

    private fun initRecyclerView(){
        if (correctModule<3){
            rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
            mAnalyseAdapter=ExamAnalyseAdapter(R.layout.item_exam_analyse_score,1,correctModule, totalAnalyseItems).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                rv_list.addItemDecoration(SpaceGridItemDeco(4,20))
            }
        }
        else{
            rv_list.layoutManager= LinearLayoutManager(this)
            mAnalyseMultiAdapter=ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score,1,totalAnalyseItems).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                rv_list.addItemDecoration(SpaceItemDeco(20))
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageUp() {
        if (isExpand){
            if (posImage>1){
                posImage-=2
            }
            else if (posImage==1){
                posImage=0
            }
        }
        else{
            if (posImage > 0) {
                posImage -= 1
            }
        }
        onChangeContent()
    }

    override fun onPageDown() {
        if (isExpand){
            if (posImage<imageCount-2){
                posImage+=2
            }
            else if (posImage==imageCount-2){
                posImage=imageCount-1
            }
        }
        else{
            if (posImage < imageCount - 1) {
                posImage += 1
            }
        }
        onChangeContent()
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent(){
        tv_page_total_a.text="$imageCount"
        tv_page_total.text="$imageCount"

        if (isExpand){
            elik_a?.setPWEnabled(true,true)
            GlideUtils.setImageUrl(this, images[posImage],v_content_a)
            val drawPath = getPathDrawStr(posImage+1)
            elik_a?.setLoadFilePath(drawPath, true)

            if (posImage+1<imageCount){
                elik_b?.setPWEnabled(true,true)
                GlideUtils.setImageUrl(this, images[posImage+1],v_content_b)
                val drawPath_b = getPathDrawStr(posImage+1+1)
                elik_b?.setLoadFilePath(drawPath_b, true)
            }
            else{
                elik_b?.setPWEnabled(false,false)
                v_content_b?.setImageResource(0)
            }
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<imageCount) "${posImage+1+1}" else ""
        }
        else{
            elik_b?.setPWEnabled(true,true)
            GlideUtils.setImageUrl(this, images[posImage],v_content_b)
            val drawPath = getPathDrawStr(posImage+1)
            elik_b?.setLoadFilePath(drawPath, true)
            tv_page.text="${posImage+1}"
        }
    }

    /**
     * 获取平均数
     */
    private fun getAverageNum(number: Double):String{
        if (number==0.0){
            return "0"
        }
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format(number)
    }

    /**
     * 文件路径
     */
    private fun getPath():String{
        return FileAddress().getPathExamDrawing(examBean!!.id)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }


    /**
     * 数据分析赋值
     */
    private fun setAnalyseData(classUserBean: ExamClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem){
        analyseItem.sort=scoreItem.sort
        analyseItem.totalScore+=MethodManager.getScore(scoreItem.score)
        analyseItem.num+=1
        if (scoreItem.result==0){
            analyseItem.wrongNum+=1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.studentName, classUserBean.score))
        }
        else{
            analyseItem.rightNum+=1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.studentName, classUserBean.score))
        }
        analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
    }

}