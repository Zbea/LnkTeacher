package com.bll.lnkteacher.ui.fragment.exam

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectBean
import com.bll.lnkteacher.ui.adapter.ExamCorrectAdapter
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class ExamCorrectFragment:BaseMainFragment(){

    private var mAdapter:ExamCorrectAdapter?=null
    private var items= mutableListOf<ExamCorrectBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        items.add(ExamCorrectBean())
        items.add(ExamCorrectBean())
        items.add(ExamCorrectBean())
        initRecyclerView()

        initDialog(2)
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        mAdapter= ExamCorrectAdapter(R.layout.item_exam_correct,items)
        rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(2,100))
    }

}