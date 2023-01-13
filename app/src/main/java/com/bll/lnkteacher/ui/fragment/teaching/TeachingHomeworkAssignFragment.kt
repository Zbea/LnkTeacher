package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.HomeworkPublishDialog
import com.bll.lnkteacher.mvp.model.HomeworkType
import com.bll.lnkteacher.ui.activity.teaching.HomeworkAssignContentActivity
import com.bll.lnkteacher.ui.adapter.HomeworkAssignAdapter
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_homework_assign.*

class TeachingHomeworkAssignFragment:BaseFragment() {

    private var mAdapter:HomeworkAssignAdapter?=null
    private var types= mutableListOf<HomeworkType>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_homework_assign
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){

        var homeworkType=HomeworkType()
        homeworkType.id=0
        homeworkType.name="家庭作业本"
        homeworkType.resId=R.mipmap.icon_homework_cover_1
        homeworkType.state=0
        types.add(homeworkType)

        var homeworkType1=HomeworkType()
        homeworkType1.id=1
        homeworkType1.name="课辅习题集"
        homeworkType1.state=1
        homeworkType1.resId=R.mipmap.icon_homework_cover_2
        types.add(homeworkType1)

        mAdapter = HomeworkAssignAdapter(R.layout.item_homework_assign, types)
        rv_list.layoutManager = GridLayoutManager(activity,3)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,40))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            if(types[position].state==0){
                HomeworkPublishDialog(requireContext()).builder()
            }
            else{
                startActivity(Intent(activity, HomeworkAssignContentActivity::class.java).putExtra("title",types[position].name))
            }
        }

    }

    fun refreshData(item:HomeworkType){
        types.add(item)
        mAdapter?.setNewData(types)
    }


}