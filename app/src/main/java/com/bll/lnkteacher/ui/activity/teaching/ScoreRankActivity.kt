package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.exam.ExamList.ExamBean
import com.bll.lnkteacher.mvp.model.exam.ExamRankList
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.RankBean
import com.bll.lnkteacher.mvp.presenter.TestPaperRankPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassAdapter
import com.bll.lnkteacher.ui.adapter.TestPaperGradeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.FlowLayoutManager
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.ac_testpaper_grade.*

class ScoreRankActivity:BaseActivity(),IContractView.ITestPaperRankView{

    private var type=0
    private var mPresenter= TestPaperRankPresenter(this,3)
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

        type=intent.flags
        if (type==0){
            val correctBean=intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
            for (item in correctBean.examList){
                mExamClassGroups.add(ClassGroup().apply {
                    classId=item.classId
                    name=item.name
                })
            }
            mPresenter.getPaperGrade(correctBean.id)
        }
        else{
            val examBean=intent.getBundleExtra("bundle")?.get("examBean") as ExamBean
            for (item in examBean.classList){
                mExamClassGroups.add(ClassGroup().apply {
                    classId=item.classId
                    name=item.className
                })
            }
            mPresenter.getExamGrade(examBean.id)
        }
    }

    override fun initView() {
        setPageTitle("成绩统计")

        initRecyclerView()
        initRecyclerViewClass()
    }

    private fun initRecyclerViewClass(){
        rv_class.layoutManager = FlowLayoutManager()//创建布局管理
        mClassAdapter = ClassAdapter(R.layout.item_class, mExamClassGroups)
        rv_class.adapter = mClassAdapter
        mClassAdapter?.bindToRecyclerView(rv_class)
        mClassAdapter?.setOnItemClickListener { adapter, view, position ->
            val item=mExamClassGroups[position]
            item.isCheck=!item.isCheck
            mClassAdapter?.notifyItemChanged(position)
            changeClassGroup()
        }
    }

    private fun initRecyclerView(){
        mAdapter = TestPaperGradeAdapter(R.layout.item_testpaper_grade,grades)
        rv_list.layoutManager = GridLayoutManager(this,4)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this,40f),0))
    }

    private fun changeClassGroup(){
        val ids= mutableListOf<Int>()
        for (item in mExamClassGroups){
            if (item.isCheck)
                ids.add(item.classId)
        }
        var items= mutableListOf<RankBean>()
        if (ids.size>0){
            for (item in grades){
                if (ids.contains(item.classId)){
                    items.add(item)
                }
            }
        }
        else{
            items=grades
        }
        mAdapter?.setNewData(items)
    }

}