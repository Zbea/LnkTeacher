package com.bll.lnkteacher.ui.fragment.learning

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IExamListView
import com.bll.lnkteacher.ui.adapter.LearningExamAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class LearningExamFragment:BaseFragment(),IExamListView{
    private var mPresenter=ExamListPresenter(this,1)
    private var mAdapter:LearningExamAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()

    override fun onList(list: ExamList) {
        items=list.list
        mAdapter?.setNewData(items)
        setPageNumber(list.total)
    }

    override fun onDeleteSuccess() {
    }
    override fun onSendSuccess() {
    }
    override fun onExamClassUser(classUserList: ExamClassUserList?) {
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

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = LearningExamAdapter(R.layout.item_learning_condition, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(requireActivity(),1,items[position].examUrl.split(",")).builder()
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(requireActivity(),30f)))
    }


    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        mPresenter.getExamList(map)
    }

}