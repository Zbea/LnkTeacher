package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.HomeworkWork
import com.bll.lnkteacher.ui.activity.teaching.HomeworkWorkActivity
import com.bll.lnkteacher.ui.adapter.HomeworkWorkAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_homework_work.*

class TeachingHomeworkWorkFragment:BaseFragment() {

    private var mAdapter:HomeworkWorkAdapter?=null
    private var position=0
    private var homeworks= mutableListOf<HomeworkWork>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_homework_work
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        var homeworkWork= HomeworkWork()
        homeworkWork.id=0
        homeworkWork.type="家庭作业本"
        homeworkWork.createDate=System.currentTimeMillis()
        homeworkWork.commitDate=System.currentTimeMillis()
        homeworkWork.content="语文作业第1、3、5页"

        var lists= mutableListOf<HomeworkWork.ListBean>()

        for (i in 0..3){
            var list= HomeworkWork().ListBean()
            list.className="三年$i 班"
            list.number=50
            list.receiveNumber=46
            lists.add(list)
        }
        homeworkWork.lists=lists
        homeworks.add(homeworkWork)
        homeworks.add(homeworkWork)
        homeworks.add(homeworkWork)

        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = HomeworkWorkAdapter(R.layout.item_teaching_homework_work, homeworks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(activity,40f), 0))
        mAdapter?.setOnChildClickListener(object : HomeworkWorkAdapter.OnChildClickListener {
            override fun onClick(view: View, parentPosition: Int, position: Int) {
                this@TeachingHomeworkWorkFragment.position =parentPosition
                startActivity(Intent(activity, HomeworkWorkActivity::class.java))
            }
        })

    }

}