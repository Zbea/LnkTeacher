package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.TestPaperCorrect
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAnalyseActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperCorrectActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperGradeActivity
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_testpaper_work.*

class TestPaperCorrectFragment:BaseFragment(),IContractView.ITestPaperCorrectView {

    private val mPresenter=TestPaperCorrectPresenter(this)
    private var mAdapter: TestPaperCorrectAdapter?=null
    private var works= mutableListOf<TestPaperCorrect.CorrectBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_testpaper_work
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        val testPaperCorrect= TestPaperCorrect.CorrectBean()
        testPaperCorrect.id=0
        testPaperCorrect.type="校级联考"
        testPaperCorrect.testPaperType="单元测试卷"
        testPaperCorrect.createDate=System.currentTimeMillis()
        testPaperCorrect.commitDate=System.currentTimeMillis()

        val lists= mutableListOf<TestPaperCorrect.ListBean>()

        for (i in 0..3){
            val list= TestPaperCorrect.ListBean()
            list.className="三年$i 班"
            list.number=50
            list.receiveNumber=46
            lists.add(list)
        }
        testPaperCorrect.lists=lists
        works.add(testPaperCorrect)
        works.add(testPaperCorrect)
        works.add(testPaperCorrect)

        mAdapter = TestPaperCorrectAdapter(R.layout.item_testpaper_correct, works).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(0,DP2PX.dip2px(activity,40f)))
            setOnItemChildClickListener { _, view, position ->
                if (view.id==R.id.tv_data){
                    startActivity(Intent(activity, TestPaperGradeActivity::class.java))
                }
                if (view.id==R.id.tv_analyse){
                    val intent=Intent(activity, TestPaperAnalyseActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("TestPaperWork",works[position])
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
            }
            setOnChildClickListener(object : TestPaperCorrectAdapter.OnChildClickListener {
                override fun onClick(view: View, position: Int) {
                    startActivity(Intent(activity, TestPaperCorrectActivity::class.java))
                }
            })
        }


    }
}