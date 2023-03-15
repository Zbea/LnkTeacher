package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperGradeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDecoGrade
import kotlinx.android.synthetic.main.ac_testpaper_grade.*
import kotlinx.android.synthetic.main.common_title.*

class TestPaperGradeActivity:BaseActivity(),IContractView.ITestPaperCorrectDetailsView{

    private val mPresenter= TestPaperCorrectDetailsPresenter(this)
    private var popClasss= mutableListOf<PopupBean>()
    private var mAdapter: TestPaperGradeAdapter?=null
    private var datas= mutableListOf<TestPaperGrade>()
    private var testPaperCorrect: TestPaperCorrect.CorrectBean?=null


    override fun onImageList(list: MutableList<TestPaper.ListBean>?) {
    }
    override fun onClassPapers(bean: TestPaperCorrectClass?) {
    }
    override fun onGrade(list: MutableList<TestPaperGrade>?) {
        if (list != null) {
            datas=list
        }
        mAdapter?.setNewData(datas)
    }
    override fun onCorrectSuccess() {
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_grade
    }

    override fun initData() {
        testPaperCorrect=intent.getBundleExtra("bundle").get("paperCorrect") as TestPaperCorrect.CorrectBean

        for (item in testPaperCorrect?.examList!!){
            popClasss.add(PopupBean(item.examChangeId,item.name,false))
        }

        mPresenter.getPaperGrade(testPaperCorrect?.id!!)
    }

    override fun initView() {
        setPageTitle("成绩统计")

        initRecyclerView()
        showView(tv_class)

        tv_class.setOnClickListener {
            showClassGroup()
        }
    }


    private fun initRecyclerView(){

        mAdapter = TestPaperGradeAdapter(R.layout.item_testpaper_grade,datas)
        rv_list.layoutManager = GridLayoutManager(this,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDecoGrade(DP2PX.dip2px(this,40f),0))

    }

    /**
     * 班级选择
     */
    private fun showClassGroup(){
        PopupRadioList(this, popClasss, tv_class,tv_class.width,  5).builder()
        ?.setOnSelectListener { item->
            tv_class.text=item.name
            val items= mutableListOf<TestPaperGrade>()
            for (ite in datas){
                if (item.id==ite.examChangeId){
                    items.add(ite)
                }
            }
            mAdapter?.setNewData(items)
        }
    }



}