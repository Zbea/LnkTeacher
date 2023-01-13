package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.TestPaperWork
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAnalyseActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperGradeActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperWorkActivity
import com.bll.lnkteacher.ui.adapter.TestPaperWorkAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_testpaper_work.*

class TeachingTestPaperWorkFragment:BaseFragment() {

    private var mAdapter: TestPaperWorkAdapter?=null
    private var position=0
    private var works= mutableListOf<TestPaperWork>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_testpaper_work
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        var testPaperWork= TestPaperWork()
        testPaperWork.id=0
        testPaperWork.type="校级联考"
        testPaperWork.testPaperType="单元测试卷"
        testPaperWork.createDate=System.currentTimeMillis()
        testPaperWork.commitDate=System.currentTimeMillis()

        var lists= mutableListOf<TestPaperWork.ListBean>()

        for (i in 0..3){
            var list=TestPaperWork().ListBean()
            list.className="三年$i 班"
            list.number=50
            list.receiveNumber=46
            lists.add(list)
        }
        testPaperWork.lists=lists
        works.add(testPaperWork)
        works.add(testPaperWork)
        works.add(testPaperWork)

        mAdapter = TestPaperWorkAdapter(R.layout.item_testpaper_work, works)
        rv_list.layoutManager = LinearLayoutManager(activity)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,DP2PX.dip2px(activity,40f)))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
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
        mAdapter?.setOnChildClickListener(object : TestPaperWorkAdapter.OnChildClickListener {
            override fun onClick(view: View, position: Int) {
                startActivity(Intent(activity, TestPaperWorkActivity::class.java))
            }
        })

    }
}