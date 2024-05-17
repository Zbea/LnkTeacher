package com.bll.lnkteacher.ui.activity.exam

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
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.GradeRankActivity
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

class ExamAnalyseActivity:BaseDrawingActivity(),IContractView.IExamListView {
    private var mPresenter=ExamListPresenter(this,3)
    private var classId=0
    private var posImage=0
    private var imageCount=0
    private var examBean: ExamList.ExamBean?=null
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()
    private var mAdapter:ExamScoreAnalyseAdapter?=null
    private var examScoreItems= mutableListOf<ExamScoreItem>()
    private val scoreItemMap=HashMap<Int, ItemList>() //所有列表列表以及题目总分、人数
    private var popScore= mutableListOf<PopupBean>()

    override fun onList(list: ExamList) {
    }
    override fun onExamImage(item: ExamList.ExamBean) {
        if (item.examUrl.isNullOrEmpty()){
            elik_a?.setPWEnabled(false,false)
            elik_b?.setPWEnabled(false,false)
        }
        else{
            images=item.examUrl.split(",").toMutableList()
            imageCount=images.size
            setContentImage()
        }
    }
    override fun onExamClassUser(classUserList: ExamClassUserList) {
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
        for (userItem in classUserList.list){
            if (userItem.studentUrl.isNullOrEmpty()){
                userItem.status=3
            }
            else{
                userItem.status=1
            }
            if (userItem.teacherUrl.isNotEmpty()){
                userItem.status=2
            }

            if (!userItem.question.isNullOrEmpty()){
                val examScoreItems= Gson().fromJson(userItem.question, object : TypeToken<List<ExamScoreItem>>() {}.type) as MutableList<ExamScoreItem>
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

        tv_score_pop.text=popScore[1].name+"分"
        tv_score_info.text="以上"
        tv_num.text=popScore[1].id.toString()

        if (totalNum>0) {
            tv_average_score.text=getAverageNum(totalScore.toDouble()/totalNum)
        }
        else{
            tv_average_score.text=""
        }

        for (key in scoreItemMap.keys){
            examScoreItems.add(ExamScoreItem().apply {
                sort=key
                score=getAverageNum(scoreItemMap[key]?.score!!.toDouble()/ scoreItemMap[key]?.num!!)
            })
        }
        mAdapter?.setNewData(examScoreItems)
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamList.ExamBean
        for (item in examBean?.classList!!){
            popClasss.add(PopupBean(item.classId,item.className,false))
        }
        classId=popClasss[0].id

        val map=HashMap<String,Any>()
        map["schoolExamJobId"]=examBean!!.id
        mPresenter.getExamImage(map)

        val map1=HashMap<String,Any>()
        map1["schoolExamJobId"]=examBean!!.id
        map1["classId"]=classId
        mPresenter.getExamClassUser(map1)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        showView(tv_class)
        disMissView(iv_tool,iv_catalog,ll_correct)
        setPageSetting("成绩统计")

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true

        tv_setting.setOnClickListener {
            val intent= Intent(this, GradeRankActivity::class.java)
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
        return FileAddress().getPathExamDrawing(examBean!!.id)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int):String{
        return getPath()+"/draw${index}.tch"//手绘地址
    }

}