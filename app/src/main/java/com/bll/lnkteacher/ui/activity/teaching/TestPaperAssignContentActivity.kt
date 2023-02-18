package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.HomeworkAssignContent
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.adapter.HomeworkAssignContentAdapter
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.*
import kotlinx.android.synthetic.main.common_title.*

class TestPaperAssignContentActivity : BaseActivity() {

    private var mAdapter: HomeworkAssignContentAdapter? = null
    private var items = mutableListOf<HomeworkAssignContent>()
    private var popWindowArea: PopupRadioList? = null
    private var popWindowClass: PopupRadioList? = null
    private var popWindowSchool: PopupRadioList? = null

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {

        for (i in 0..10) {
            var homeworkAssignContent = HomeworkAssignContent()
            homeworkAssignContent.name = "数学公式$i"
            homeworkAssignContent.id = i
            items.add(homeworkAssignContent)
        }

    }

    override fun initView() {
        setPageTitle(intent.getStringExtra("title") + "考试卷")
        showView(tv_send)

        rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
        mAdapter = HomeworkAssignContentAdapter(R.layout.item_testpaper_assign_content, items)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)

        tv_class_name.setOnClickListener {
            selectorClassGroup()
        }

        tv_school_name.setOnClickListener {
            selectorSchoolGroup()
        }

        tv_group_name.setOnClickListener {
            selectorAreaGroup()
        }

    }

    /**
     * 班级选择
     */
    private fun selectorClassGroup() {
        val pops = DataBeanManager.popClassGroups
        if (popWindowClass == null) {
            popWindowClass = PopupRadioList(this, pops, tv_class_name,tv_class_name.width,  5).builder()
            popWindowClass?.setOnSelectListener { item ->
                tv_class_name.text = item.name
            }
        } else {
            popWindowClass?.show()
        }
    }

    /**
     * 校群选择
     */
    private fun selectorSchoolGroup() {
        val pops = mutableListOf<PopupBean>()
        val groups = DataBeanManager.schoolGroups
        for (i in 0 until groups.size) {
            pops.add(PopupBean(groups[i].id, groups[i].schoolName, false))
        }
        if (popWindowSchool == null) {
            popWindowSchool = PopupRadioList(this, pops, tv_school_name,tv_school_name.width,  5).builder()
            popWindowSchool?.setOnSelectListener { item ->
                tv_school_name.text = item.name
            }
        } else {
            popWindowSchool?.show()
        }
    }

    /**
     * 际群选择
     */
    private fun selectorAreaGroup() {
        val pops = mutableListOf<PopupBean>()
        val groups = DataBeanManager.areaGroups
        for (i in 0 until groups.size) {
            pops.add(PopupBean(groups[i].id, groups[i].schoolName, false))
        }
        if (popWindowArea == null) {
            popWindowArea = PopupRadioList(this, pops, tv_group_name,tv_group_name.width,  5).builder()
            popWindowArea?.setOnSelectListener { item ->
                tv_group_name.text = item.name
            }
        } else {
            popWindowArea?.show()
        }
    }

}