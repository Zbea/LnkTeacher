package com.bll.lnkteacher.ui.fragment.learning

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView.ITestPaperCorrectView
import com.bll.lnkteacher.ui.adapter.LearningHomeworkAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class LearningHomeworkFragment:BaseFragment(),ITestPaperCorrectView {

    private var mPresenter=TestPaperCorrectPresenter(this,1)
    private var mAdapter:LearningHomeworkAdapter?=null
    private var items= mutableListOf<CorrectBean>()

    override fun onList(list: CorrectList) {
        setPageNumber(list.total)
        items= list.list
        mAdapter?.setNewData(items)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=7
        initRecyclerView()
        initDialog(1)
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight = 1f
        layoutParams.setMargins(DP2PX.dip2px(activity, 40f), DP2PX.dip2px(activity, 40f), DP2PX.dip2px(activity, 40f), 0)
        rv_list.layoutParams = layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = LearningHomeworkAdapter(R.layout.item_learning_condition, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
        }
        rv_list.addItemDecoration(SpaceItemDeco( DP2PX.dip2px(activity, 30f)))
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {
            val map = HashMap<String, Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["taskType"] = 1
            mPresenter.getHomeworkCorrectList(map)
        }
    }


}