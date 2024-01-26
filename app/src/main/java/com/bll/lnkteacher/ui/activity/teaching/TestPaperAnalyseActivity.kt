package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.*
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamScoreAnalyseAdapter
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_title.*
import java.text.DecimalFormat

class TestPaperAnalyseActivity:BaseActivity(),IContractView.ITestPaperCorrectDetailsView {

    private val mPresenter= TestPaperCorrectDetailsPresenter(this)
    private var classId=0
    private var index=0//当前学生的作业下标
    private var correctList: CorrectBean?=null
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()
    private var mAdapter:ExamScoreAnalyseAdapter?=null
    private var examScoreItems= mutableListOf<ExamScoreItem>()
    private val scoreItemMap=HashMap<Int, ItemList>() //所有列表列表以及题目总分、人数
    private var popScore= mutableListOf<PopupBean>()

    override fun onImageList(list: MutableList<ContentListBean>?) {
        if (list != null) {
            for (item in list){
                images.add(item.url)
            }
            setImageContent()
        }
    }
    override fun onClassPapers(bean: TestPaperCorrectClass) {
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
                else if (userItem.score>=60){
                    score60+=1
                }
                else if (userItem.score>=70){
                    score70+=1
                }
                else if (userItem.score>=80){
                    score80+=1
                }
                else if (userItem.score>=90){
                    score90+=1
                }
                else{
                    score100+=1
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
    override fun onGrade(list: MutableList<TestPaperGrade>?) {

    }
    override fun onCorrectSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
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
        showView(tv_class,tv_setting)

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true
        tv_setting.text="成绩统计"

        iv_up.setOnClickListener {
            if (index>0){
                index-=1
                setImageContent()
            }
        }
        iv_down.setOnClickListener {
            if (index<images.size-1){
                index+=1
                setImageContent()
            }
        }

        tv_setting.setOnClickListener {
            val intent= Intent(this, TestPaperGradeActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("paperCorrect",correctList)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

        tv_class.setOnClickListener {
            PopupRadioList(this, popClasss, tv_class, tv_class.width ,5).builder()
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
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ExamScoreAnalyseAdapter(R.layout.item_exam_score_analyse, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, 30))
        }
    }

    /**
     * 获取平均数
     */
    private fun getAverageNum(number: Double):String{
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format(number)
    }

    /**
     * 设置作业内容
     */
    private fun setImageContent(){
        if (index<images.size){
            GlideUtils.setImageRoundUrl(this, images[index],iv_image,10)
            tv_page.text="${index+1}/${images.size}"
        }
    }

}