package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.AnalyseUserDetailsDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamAnalyseAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseMultiAdapter
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_correct_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DecimalFormat

class AnalyseActivity:BaseDrawingActivity(),IContractView.ITestPaperCorrectDetailsView {

    private var mPresenter=TestPaperCorrectDetailsPresenter(this,3)
    private var classId=0
    private var posImage=0
    private var imageCount=0
    private var subType=0
    private var correctList: CorrectBean?=null
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()
    private var scoreItems= mutableListOf<ScoreItem>()
    private var popScore= mutableListOf<PopupBean>()
    private var totalAnalyseItems= mutableListOf<AnalyseItem>() //题目集合
    private var mAnalyseAdapter:ExamAnalyseAdapter?=null
    private var mAnalyseMultiAdapter:ExamAnalyseMultiAdapter?=null

    override fun onClassPapers(bean: TestPaperClassUserList) {
        scoreItems.clear()
        popScore.clear()
        totalAnalyseItems.clear()
        var totalScore=0
        var totalNum=0
        var score0=0
        var score60=0
        var score70=0
        var score80=0
        var score90=0
        var score100=0
        for (userItem in bean.list){
            correctModule=userItem.questionType
            if (!userItem.question.isNullOrEmpty()&&userItem.status==2&&correctModule>0){
                currentScores= jsonToList(userItem.question) as MutableList<ScoreItem>
                for (item in currentScores){
                    val currentScore=getScore(item.score)
                    if (correctModule<3){
                        if (totalAnalyseItems.size<currentScores.size){
                            val analyseItem= AnalyseItem()
                            setAnalyseData(userItem,item,analyseItem)
                            totalAnalyseItems.add(analyseItem)
                        }
                        else{
                            val examAnalyseItem=totalAnalyseItems[item.sort-1]
                            setAnalyseData(userItem,item,examAnalyseItem)
                        }
                    }
                    else{
                        if (totalAnalyseItems.size<currentScores.size){
                            val analyseItem= AnalyseItem()
                            analyseItem.sort=item.sort
                            analyseItem.totalScore+=currentScore
                            analyseItem.num+=1
                            analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
                            val childAnalyseItems= mutableListOf<AnalyseItem>()
                            for (childItem in item.childScores){
                                val childAnalyseItem= AnalyseItem()
                                setAnalyseData(userItem,childItem,childAnalyseItem)
                                childAnalyseItems.add(childAnalyseItem)
                            }
                            analyseItem.childAnalyses=childAnalyseItems
                            totalAnalyseItems.add(analyseItem)
                        }
                        else{
                            val examAnalyseItem=totalAnalyseItems[item.sort-1]
                            examAnalyseItem.totalScore+=currentScore
                            examAnalyseItem.num+=1
                            examAnalyseItem.averageScore=examAnalyseItem.totalScore/examAnalyseItem.num
                            for (childItem in item.childScores){
                                val index=item.childScores.indexOf(childItem)
                                val childExamAnalyseItem=examAnalyseItem.childAnalyses[index]
                                setAnalyseData(userItem,childItem,childExamAnalyseItem)
                            }
                        }
                    }
                }
            }
            //已批改
            if (userItem.status==2){
                totalNum+=1
                totalScore+=userItem.score

                if (userItem.score<60){
                    score0+=1
                }
                else{
                    score60+=1
                    if (userItem.score>=70){
                        score70+=1
                        if (userItem.score>=80){
                            score80+=1
                            if (userItem.score>=90){
                                score90+=1
                                if (userItem.score>=100){
                                    score100+=1
                                }
                            }
                        }
                    }
                }
            }
        }

        //统计分数
        for (item in DataBeanManager.scoreList){
            when(item){
                0->{
                    popScore.add(PopupBean(score0,item.toString(),false))
                }
                60->{
                    popScore.add(PopupBean(score60,item.toString(),true))
                }
                70->{
                    popScore.add(PopupBean(score70,item.toString(),false))
                }
                80->{
                    popScore.add(PopupBean(score80,item.toString(),false))
                }
                90->{
                    popScore.add(PopupBean(score90,item.toString(),false))
                }
                else->{
                    popScore.add(PopupBean(score100,item.toString(),false))
                }
            }
        }

        tv_correct_number.text=bean.totalUpdate.toString()
        tv_score_pop.text=popScore[1].name+"分"
        tv_score_info.text="以上"
        tv_num.text=popScore[1].id.toString()

        if (totalNum>0) {
            tv_average_score.text=getAverageNum(totalScore.toDouble()/totalNum)
        }
        else{
            tv_average_score.text=""
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
    override fun onCorrectSuccess() {
    }

    override fun onSendSuccess() {
    }



    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        correctModule=intent.getIntExtra("module",-1)
        subType = intent.getIntExtra("subType", 0)
        correctList= intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        images= correctList?.examList?.get(0)?.imageUrl?.split(",") as MutableList<String>
        for (item in correctList?.examList!!){
            popClasss.add(PopupBean(item.examChangeId,item.name,false))
        }
        classId=popClasss[0].id

        mPresenter.getClassPapers(classId)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        showView(tv_class)
        disMissView(iv_tool,iv_catalog,iv_btn)
        setPageSetting("成绩统计")

        if (correctModule>0&&subType!=2){
            setPageCustom("因材施教")
            showView(rv_list)
        }

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true

        tv_custom.setOnClickListener {
            val intent= Intent(this, AnalyseTeachingActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("paperCorrect",correctList)
            intent.putExtra("bundle",bundle)
            intent.putExtra("module",correctModule)
            intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_LEFT)
            customStartActivity(intent)
        }

        tv_setting.setOnClickListener {
            val intent= Intent(this, GradeRankActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("paperCorrect",correctList)
            intent.putExtra("bundle",bundle)
            intent.flags=0
            intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
            customStartActivity(intent)
        }

        tv_class.setOnClickListener {
            PopupRadioList(this, popClasss, tv_class ,5).builder()
                .setOnSelectListener { item ->
                    tv_class.text = item.name
                    if (classId!=item.id){
                        classId=item.id
                        mPresenter.getClassPapers(classId)
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

        onChangeExpandView()

        initRecyclerView()

        if (images.size>0){
            imageCount=images.size
            onChangeContent()
        }
    }

    private fun initRecyclerView(){
        if (correctModule<3){
            rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
            mAnalyseAdapter=ExamAnalyseAdapter(R.layout.item_exam_analyse_score,correctModule, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                rv_list.addItemDecoration(SpaceGridItemDeco(4,20))
                setOnItemChildClickListener { adapter, view, position ->
                    if (view.id==R.id.tv_wrong_num){
                        val students=totalAnalyseItems[position].wrongStudents
                        AnalyseUserDetailsDialog(this@AnalyseActivity,students).builder()
                    }
                }
            }
        }
        else{
            rv_list.layoutManager= LinearLayoutManager(this)
            mAnalyseMultiAdapter=ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score,null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                rv_list.addItemDecoration(SpaceItemDeco(0,0,0,20))
                setCustomItemChildClickListener{position, view, childPosition ->
                    if (view.id==R.id.tv_wrong_num){
                        val students=totalAnalyseItems[position].childAnalyses[childPosition].wrongStudents
                        AnalyseUserDetailsDialog(this@AnalyseActivity,students).builder()
                    }
                }
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
        return FileAddress().getPathTestPaperDrawing(correctList!!.id)
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
    private fun setAnalyseData(classUserBean: TestPaperClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem){
        analyseItem.sort=scoreItem.sort
        analyseItem.totalScore+=getScore(scoreItem.score)
        analyseItem.num+=1
        if (scoreItem.result==0){
            analyseItem.wrongNum+=1
            analyseItem.wrongStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        else{
            analyseItem.rightNum+=1
            analyseItem.rightStudents.add(AnalyseItem.UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().finishActivity(AnalyseTeachingActivity::class.java.name)
    }

}