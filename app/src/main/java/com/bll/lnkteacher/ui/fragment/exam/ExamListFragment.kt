package com.bll.lnkteacher.ui.fragment.exam

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.ui.adapter.ExamListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class ExamListFragment:BaseFragment(){
    private var mAdapter:ExamListAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=3
        initRecyclerView()
    }

    override fun lazyLoad() {
        items.add(ExamList().ExamBean())
        items.add(ExamList().ExamBean())
        items.add(ExamList().ExamBean())
        setPageNumber(3)
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