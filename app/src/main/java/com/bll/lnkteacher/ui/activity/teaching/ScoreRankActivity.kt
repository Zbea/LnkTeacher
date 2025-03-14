package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.PopupCheckList
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
import kotlinx.android.synthetic.main.ac_testpaper_grade.iv_arrow_page_down
import kotlinx.android.synthetic.main.ac_testpaper_grade.iv_arrow_page_up
import kotlinx.android.synthetic.main.ac_testpaper_grade.rv_list
import kotlinx.android.synthetic.main.common_title.tv_class

class ScoreRankActivity:BaseAppCompatActivity(),IContractView.ITestPaperRankView{

    private var type=0
    private var mPresenter= TestPaperRankPresenter(this,3)
    private var mAdapter: TestPaperGradeAdapter?=null
    private var grades= mutableListOf<RankBean>()
    private var popClasss= mutableListOf<PopupBean>()

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
                popClasss.add(PopupBean(item.classId,item.name,true))
            }
            mPresenter.getPaperGrade(correctBean.id)
        }
        else{
            val examBean=intent.getBundleExtra("bundle")?.get("examBean") as ExamBean
            for (item in examBean.classList){
                popClasss.add(PopupBean(item.classId,item.className,true))
            }
            mPresenter.getExamGrade(examBean.id)
        }
    }

    override fun initView() {
        setPageTitle("成绩统计")
        showView(tv_class)

        tv_class.text="全部班级"
        tv_class.setOnClickListener {
            PopupCheckList(this,popClasss,tv_class,5).builder().setOnSelectListener{ items->
                if (items.size==popClasss.size){
                    tv_class.text="全部班级"
                }
                else{
                    var className=""
                    for (item in items){
                        className+=item.name+if (items.indexOf(item)==items.size-1) "" else "、"
                    }
                    tv_class.text=className
                }
                val ids= mutableListOf<Int>()
                for (item in items){
                    ids.add(item.id)
                }
                val selectRanks= mutableListOf<RankBean>()
                for (item in grades){
                    if (ids.contains(item.classId)){
                        selectRanks.add(item)
                    }
                }
                mAdapter?.setNewData(selectRanks)
            }
        }

        iv_arrow_page_up.setOnClickListener {
            rv_list.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_arrow_page_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this,100f))
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        mAdapter = TestPaperGradeAdapter(R.layout.item_testpaper_grade,grades)
        rv_list.layoutManager = GridLayoutManager(this,4)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this,40f),0))
    }

}