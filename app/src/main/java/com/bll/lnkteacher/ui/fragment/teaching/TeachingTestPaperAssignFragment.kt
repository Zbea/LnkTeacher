package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.TestPaperType
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAssignContentActivity
import com.bll.lnkteacher.ui.adapter.TestPaperAssignAdapter
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_testpaper_assign.*

class TeachingTestPaperAssignFragment:BaseFragment() {

    private var mAdapter: TestPaperAssignAdapter?=null
    private var position=0
    private var types= mutableListOf<TestPaperType>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_testpaper_assign
    }

    override fun initView() {
        initData()
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initData(){
        var testPaper= TestPaperType()
        testPaper.id=0
        testPaper.name="单元"
        testPaper.resId=R.mipmap.icon_testpaper_bg
        types.add(testPaper)

        var testPaper1=TestPaperType()
        testPaper1.id=1
        testPaper1.resId=R.mipmap.icon_testpaper_bg
        testPaper1.name="阶段"
        types.add(testPaper1)

        var testPaper2=TestPaperType()
        testPaper2.id=2
        testPaper2.resId=R.mipmap.icon_testpaper_bg
        testPaper2.name="学期"
        types.add(testPaper2)
    }

    private fun initRecyclerView(){

        mAdapter = TestPaperAssignAdapter(R.layout.item_testpaper_assign,types)
        rv_list.layoutManager = GridLayoutManager(activity,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,80))
        mAdapter?.setOnItemClickListener  { adapter, view, position ->
            startActivity(Intent(activity, TestPaperAssignContentActivity::class.java).putExtra("title",types[position].name))
        }

    }

    fun refreshData(item: TestPaperType){
        types.add(item)
        mAdapter?.setNewData(types)
    }

}