package com.bll.lnkteacher.ui.fragment.exam

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.ui.adapter.ExamListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class ExamListFragment:BaseMainFragment(){
    private var mAdapter:ExamListAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=3
        initRecyclerView()
        initDialog(2)
    }

    override fun lazyLoad() {

        val classs= mutableListOf<CorrectClassBean>()
        classs.add(CorrectClassBean().apply {
            name="三年(1)班"
            totalStudent=50
            totalSubmitStudent=50
        })
        classs.add(CorrectClassBean().apply {
            name="三年(2)班"
            totalStudent=50
            totalSubmitStudent=50
        })

        items.add(ExamList().ExamBean().apply {
            examName="（语文）期中考试卷"
            time=System.currentTimeMillis()
            examList=classs
        })
        items.add(ExamList().ExamBean().apply {
            examName="（语文）期中考试卷"
            time=System.currentTimeMillis()
            examList=classs
        })
        setPageNumber(2)
        mAdapter?.setNewData(items)
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(activity,40f),
            DP2PX.dip2px(activity,30f),
            DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = ExamListAdapter(R.layout.item_testpaper_correct, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0,0,0, DP2PX.dip2px(requireActivity(),30f)))
            setOnItemChildClickListener { _, view, position ->

            }

        }
    }

}