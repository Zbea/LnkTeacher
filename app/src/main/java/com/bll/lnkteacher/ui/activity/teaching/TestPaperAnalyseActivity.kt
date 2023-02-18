package com.bll.lnkteacher.ui.activity.teaching

import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.TestPaperWork
import com.bll.lnkteacher.utils.GlideUtils
import kotlinx.android.synthetic.main.ac_testpaper_analyse.*
import kotlinx.android.synthetic.main.ac_testpaper_work.iv_down
import kotlinx.android.synthetic.main.ac_testpaper_work.iv_image
import kotlinx.android.synthetic.main.ac_testpaper_work.iv_up
import kotlinx.android.synthetic.main.ac_testpaper_work.tv_page
import kotlinx.android.synthetic.main.common_title.*

class TestPaperAnalyseActivity:BaseActivity() {

    private var index=0//当前学生的作业下标
    private var testPaperWork:TestPaperWork?=null
    private var popScores= mutableListOf<PopupBean>()
    private var popScoresDown= mutableListOf<PopupBean>()
    private var popRanks= mutableListOf<PopupBean>()

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse
    }

    override fun initData() {
        testPaperWork=intent.getBundleExtra("bundle").get("TestPaperWork") as TestPaperWork
        val scores= DataBeanManager.scoreList
        for (i in scores.indices)
        {
            val popupBean= PopupBean(
                i,
                scores[i].toString(),
                i == 1
            )
            popScores.add(popupBean)
            popScoresDown.add(popupBean)
        }

        popRanks.add(PopupBean(0, "班级排名", true))
        popRanks.add(PopupBean(1, "年级排名", false))
        popRanks.add(PopupBean(2, "区域排名", false))

    }

    override fun initView() {
        setPageTitle("考卷分析 ${testPaperWork?.type} ${testPaperWork?.testPaperType}")
        showView(iv_manager)

        setImageContent()

        iv_up.setOnClickListener {
            if (index>0){
                index-=1
                setImageContent()
            }
        }
        iv_down.setOnClickListener {
            if (index< testPaperWork?.images?.size?.minus(1)!!){
                index+=1
                setImageContent()
            }
        }

        tv_score.text=popScores[1].name
        tv_score.setOnClickListener {
            showPopwindowScore(popScores,tv_score)
        }

        tv_score_down.text=popScoresDown[1].name
        tv_score_down.setOnClickListener {
            showPopwindowScoreDown(popScoresDown,tv_score_down)
        }

        iv_manager.setOnClickListener {
            PopupRadioList(this, popRanks, iv_manager,  10).builder()
                ?.setOnSelectListener { item ->

                }
        }
    }

    /**
     * 设置作业内容
     */
    private fun setImageContent(){
        GlideUtils.setImageRoundUrl(this, testPaperWork?.images?.get(index),iv_image,10)
        tv_page.text="${index+1}/${testPaperWork?.images?.size}"
    }

    /**
     * 分数选择 以上
     */
    private fun showPopwindowScore(list:MutableList<PopupBean>, view:TextView){
        PopupRadioList(this, list, view,  0).builder()
            ?.setOnSelectListener { item ->
                view.text = item.name
            }
    }

    /**
     * 分数选择 以下
     */
    private fun showPopwindowScoreDown(list:MutableList<PopupBean>, view:TextView){
        PopupRadioList(this, list, view,  0).builder()
            ?.setOnSelectListener { item ->
                view.text = item.name
            }
    }

}