package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamScoreAnalyseAdapter
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_correct_drawing.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DecimalFormat

class AnalyseActivity:BaseDrawingActivity(),IContractView.ITestPaperCorrectDetailsView {

    private var correctModule=-1
    private var mPresenter=TestPaperCorrectDetailsPresenter(this,3)
    private var classId=0
    private var posImage=0
    private var imageCount=0
    private var correctList: CorrectBean?=null
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()
    private var mAdapter:ExamScoreAnalyseAdapter?=null
    private var examScoreItems= mutableListOf<ExamScoreItem>()
    private val scoreItemMap=HashMap<Int, ItemList>() //所有列表列表以及题目总分、人数
    private var popScore= mutableListOf<PopupBean>()

    override fun onImageList(list: MutableList<ContentListBean>) {
        for (item in list){
            images.add(item.url)
        }
        if (images.size>0){
            imageCount=images.size
            setContentImage()
        }
    }
    override fun onClassPapers(bean: TestPaperClassUserList) {
        scoreItemMap.clear()
        examScoreItems.clear()
        popScore.clear()
        var totalScore=0
        var totalNum=0
        var score0=0
        var score60=0
        var score70=0
        var score80=0
        var score90=0
        var score100=0
        for (userItem in bean.list){
            if (!userItem.question.isNullOrEmpty()&&userItem.status==2&&correctModule>0){
                if (correctModule<3){
                    examScoreItems=Gson().fromJson(userItem.question, object : TypeToken<List<ExamScoreItem>>() {}.type) as MutableList<ExamScoreItem>
                }
                else{
                    val scores=Gson().fromJson(userItem.question, object : TypeToken<List<List<ExamScoreItem>>>() {}.type) as MutableList<List<ExamScoreItem>>
                    for (i in scores.indices){
                        examScoreItems.add(ExamScoreItem().apply {
                            sort=i+1
                            var totalItem=0
                            for (item in scores[i]){
                                if (!item.score.isNullOrEmpty()){
                                    totalItem+=item.score.toInt()
                                }
                            }
                            score=totalItem.toString()
                            childScores=scores[i]
                        })
                    }
                }

                for (item in examScoreItems){
                    var itemList=scoreItemMap[item.sort]
                    if (itemList!=null){
                        itemList.num=itemList.num+1
                        itemList.score=itemList.score+item.score.toInt()
                        scoreItemMap[item.sort]= itemList
                    }
                    else{
                        itemList=ItemList()
                        itemList.num=1
                        itemList.score=item.score.toInt()
                        scoreItemMap[item.sort]= itemList
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
            for (key in scoreItemMap.keys){
                examScoreItems.add(ExamScoreItem().apply {
                    sort=key
                    score=getAverageNum(scoreItemMap[key]?.score!!.toDouble()/ scoreItemMap[key]?.num!!)
                })
            }
            mAdapter?.setNewData(examScoreItems)
        }
    }
    override fun onCorrectSuccess() {
    }

    override fun onSendSuccess() {
    }

    override fun setModuleSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        correctModule=intent.getIntExtra("module",-1)
        correctList= intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        for (item in correctList?.examList!!){
            popClasss.add(PopupBean(item.examChangeId,item.name,false))
        }
        classId=popClasss[0].id

        mPresenter.getPaperImages(correctList?.examList!![0].taskId)
        mPresenter.getClassPapers(classId)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        showView(tv_class)
        disMissView(iv_tool,iv_catalog)
        setPageSetting("成绩统计")

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true

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
                    if (classId!=item.id)
                        mPresenter.getClassPapers(item.id)
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
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = ExamScoreAnalyseAdapter(R.layout.item_exam_score_analyse, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(4, 30))
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        setContentImage()
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
        setContentImage()
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
        setContentImage()
    }

    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage(){
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
                v_content_b.setImageResource(0)
            }
            tv_page.text="${posImage+1}/${imageCount}"
            tv_page_a.text=if (posImage+1<imageCount) "${posImage+1+1}/${imageCount}" else ""
        }
        else{
            elik_b?.setPWEnabled(true,true)
            GlideUtils.setImageUrl(this, images[posImage],v_content_b)
            val drawPath = getPathDrawStr(posImage+1)
            elik_b?.setLoadFilePath(drawPath, true)
            tv_page.text="${posImage+1}/$imageCount"
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


}