package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.HomeworkCorrect
import com.bll.lnkteacher.ui.activity.teaching.HomeworkCorrectActivity
import com.bll.lnkteacher.ui.adapter.HomeworkCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_homework_work.*

class HomeworkCorrectFragment:BaseFragment() {

    private var mAdapter:HomeworkCorrectAdapter?=null
    private var position=0
    private var homeworks= mutableListOf<HomeworkCorrect>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_homework_work
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        val homeworkCorrect= HomeworkCorrect()
        homeworkCorrect.id=0
        homeworkCorrect.type="家庭作业本"
        homeworkCorrect.createDate=System.currentTimeMillis()
        homeworkCorrect.commitDate=System.currentTimeMillis()
        homeworkCorrect.content="语文作业第1、3、5页"

        val lists= mutableListOf<HomeworkCorrect.ListBean>()

        for (i in 0..3){
            val list= HomeworkCorrect().ListBean()
            list.className="三年$i 班"
            list.number=50
            list.receiveNumber=46
            lists.add(list)
        }
        homeworkCorrect.lists=lists
        homeworks.add(homeworkCorrect)
        homeworks.add(homeworkCorrect)
        homeworks.add(homeworkCorrect)

        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mAdapter = HomeworkCorrectAdapter(R.layout.item_teaching_homework_correct, homeworks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(activity,40f), 0))
        mAdapter?.setOnChildClickListener(object : HomeworkCorrectAdapter.OnChildClickListener {
            override fun onClick(view: View, parentPosition: Int, position: Int) {
                this@HomeworkCorrectFragment.position =parentPosition
                startActivity(Intent(activity, HomeworkCorrectActivity::class.java))
            }
        })

    }

}