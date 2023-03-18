package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.homework.HomeworkCorrect
import com.bll.lnkteacher.ui.activity.teaching.HomeworkCorrectActivity
import com.bll.lnkteacher.ui.adapter.HomeworkCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class HomeworkCorrectFragment:BaseFragment() {

    private var mAdapter:HomeworkCorrectAdapter?=null
    private var position=0
    private var homeworks= mutableListOf<HomeworkCorrect>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        val homeworkCorrect=
            HomeworkCorrect()
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

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = HomeworkCorrectAdapter(R.layout.item_teaching_homework_correct, homeworks).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(activity,30f)))
            setOnChildClickListener { view, parentPosition, position ->
                this@HomeworkCorrectFragment.position = parentPosition
                startActivity(Intent(activity, HomeworkCorrectActivity::class.java))
            }
        }


    }

}