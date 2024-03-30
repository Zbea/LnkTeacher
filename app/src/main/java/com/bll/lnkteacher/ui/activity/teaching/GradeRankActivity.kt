package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.exam.ExamList.ExamBean
import com.bll.lnkteacher.mvp.model.exam.ExamRankList
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.RankBean
import com.bll.lnkteacher.mvp.presenter.TestPaperRankPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperGradeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_testpaper_grade.*
import kotlinx.android.synthetic.main.common_title.*

class GradeRankActivity:BaseActivity(),IContractView.ITestPaperRankView{

    private var type=0
    private var mPresenter= TestPaperRankPresenter(this,3)
    private var popClasss= mutableListOf<PopupBean>()
    private var mAdapter: TestPaperGradeAdapter?=null
    private var grades= mutableListOf<RankBean>()

    override fun onGrade(list: MutableList<RankBean>) {
        grades=list
        mAdapter?.setNewData(grades)
    }

    override fun onExamGrade(list: ExamRankList) {
        for (item in list.list){
            grades.add(RankBean().apply {
                classId=item.classId
                className=item.className
                score=item.score
                name=item.studentName
            })
        }
        mAdapter?.setNewData(grades)
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_grade
    }

    override fun initData() {
        popClasss.add(PopupBean(0,"全部班级",true))
        type=intent.flags
        if (type==0){
            val correctBean=intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
            for (item in correctBean.examList){
                popClasss.add(PopupBean(item.classId,item.name,false))
            }
            mPresenter.getPaperGrade(correctBean.id)
        }
        else{
            val examBean=intent.getBundleExtra("bundle")?.get("examBean") as ExamBean
            for (item in examBean.classList){
                popClasss.add(PopupBean(item.classId,item.className,false))
            }
            mPresenter.getExamGrade(examBean.id)
        }
    }

    override fun initView() {
        setPageTitle("成绩统计")

        initRecyclerView()
        showView(tv_class)

        tv_class.text=popClasss[0].name
        tv_class.setOnClickListener {
            showClassGroup()
        }
    }


    private fun initRecyclerView(){
        mAdapter = TestPaperGradeAdapter(R.layout.item_testpaper_grade,grades)
        rv_list.layoutManager = GridLayoutManager(this,4)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this,40f),0))
    }

    /**
     * 班级选择
     */
    private fun showClassGroup(){
        PopupRadioList(this, popClasss, tv_class,tv_class.width,  5).builder()
        .setOnSelectListener { item->
            tv_class.text=item.name
            if (item.id==0){
                mAdapter?.setNewData(grades)
                return@setOnSelectListener
            }
            val items= mutableListOf<RankBean>()
            for (ite in grades){
                if (item.id==ite.classId){
                    items.add(ite)
                }
            }
            mAdapter?.setNewData(items)
        }
    }



}