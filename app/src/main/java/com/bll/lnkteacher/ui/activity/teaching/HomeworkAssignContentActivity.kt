package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.HomeworkPublishClassgroupSelectorDialog
import com.bll.lnkteacher.mvp.model.HomeworkAssignContent
import com.bll.lnkteacher.ui.adapter.HomeworkAssignContentAdapter
import kotlinx.android.synthetic.main.ac_homework_assgin_content.*

class HomeworkAssignContentActivity:BaseActivity() {

    private var mAdapter:HomeworkAssignContentAdapter?=null
    private var items= mutableListOf<HomeworkAssignContent>()

    override fun layoutId(): Int {
        return R.layout.ac_homework_assgin_content
    }

    override fun initData() {

        for (i in 0..10){
            var homeworkAssignContent=HomeworkAssignContent()
            homeworkAssignContent.name= "数学公式$i"
            homeworkAssignContent.id=i
            items.add(homeworkAssignContent)
        }

    }

    override fun initView() {
        setPageTitle(intent.getStringExtra("title"))

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = HomeworkAssignContentAdapter(R.layout.item_homework_assign_content, items)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)


        tv_class_name.setOnClickListener {
            HomeworkPublishClassgroupSelectorDialog(this).builder()
                ?.setOnDialogClickListener { }
        }
    }
}