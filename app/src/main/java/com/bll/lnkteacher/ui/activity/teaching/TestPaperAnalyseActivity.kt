package com.bll.lnkteacher.ui.activity.teaching

import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrectClass
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperGrade
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.common_title.*

class TestPaperAnalyseActivity:BaseActivity(),IContractView.ITestPaperCorrectDetailsView {

    private val mPresenter= TestPaperCorrectDetailsPresenter(this)
    private var index=0//当前学生的作业下标
    private var correctList: CorrectBean?=null
    private var popScores= mutableListOf<PopupBean>()
    private var popScoresDown= mutableListOf<PopupBean>()
    private val popClasss= mutableListOf<PopupBean>()
    private var images= mutableListOf<String>()


    override fun onImageList(list: MutableList<ContentListBean>?) {
        if (list != null) {
            for (item in list){
                images.add(item.url)
            }
            setImageContent()
        }
    }
    override fun onClassPapers(bean: TestPaperCorrectClass?) {
    }
    override fun onGrade(list: MutableList<TestPaperGrade>?) {

    }
    override fun onCorrectSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        correctList=intent.getBundleExtra("bundle").get("paperCorrect") as CorrectBean
        val scores= DataBeanManager.scoreList
        for (i in scores.indices)
        {
            val popupBean= PopupBean(i, scores[i].toString(), i == 1)
            popScores.add(popupBean)
            popScoresDown.add(popupBean)
        }

        for (item in correctList?.examList!!){
            popClasss.add(PopupBean(item.examChangeId,item.name,false))
        }

        mPresenter.getPaperImages(correctList?.examList!![0].taskId)
    }

    override fun initView() {
        setPageTitle(R.string.teaching_testpaper_analyse)
        showView(tv_class)

        tv_class.text=popClasss[0].name
        popClasss[0].isCheck=true

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

        tv_score.text=popScores[1].name
        tv_score.setOnClickListener {
            showPopWindowScore(popScores,tv_score)
        }

        tv_score_down.text=popScoresDown[1].name
        tv_score_down.setOnClickListener {
            showPopWindowScoreDown(popScoresDown,tv_score_down)
        }

        tv_class.setOnClickListener {
            PopupRadioList(this, popClasss, tv_class, tv_class.width ,5).builder()
                ?.setOnSelectListener { item ->
                    tv_class.text = item.name
                }
        }


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

    /**
     * 分数选择 以上
     */
    private fun showPopWindowScore(list:MutableList<PopupBean>, view:TextView){
        PopupRadioList(this, list, view,  0).builder()
            ?.setOnSelectListener { item ->
                view.text = item.name
            }
    }

    /**
     * 分数选择 以下
     */
    private fun showPopWindowScoreDown(list:MutableList<PopupBean>, view:TextView){
        PopupRadioList(this, list, view,  0).builder()
            ?.setOnSelectListener { item ->
                view.text = item.name
            }
    }

}