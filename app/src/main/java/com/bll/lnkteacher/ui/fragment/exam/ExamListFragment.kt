package com.bll.lnkteacher.ui.fragment.exam

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IExamListView
import com.bll.lnkteacher.ui.activity.exam.ExamAnalyseActivity
import com.bll.lnkteacher.ui.activity.exam.ExamDetailsActivity
import com.bll.lnkteacher.ui.adapter.ExamListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class ExamListFragment:BaseFragment(),IExamListView{
    private var mPresenter=ExamListPresenter(this,2)
    private var mAdapter:ExamListAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()

    override fun onList(list: ExamList) {
        items=list.list
        mAdapter?.setNewData(items)
        setPageNumber(list.total)
    }
    override fun onExamClassUser(classUserList: ExamClassUserList?) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=3
        initRecyclerView()
        initDialog(2)
    }

    override fun lazyLoad() {
        fetchData()
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
            rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(requireActivity(),20f)))
            setOnItemChildClickListener { _, view, position ->
                if (view.id==R.id.tv_analyse){
                    val intent= Intent(requireActivity(), ExamAnalyseActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("examBean",items[position])
                    intent.putExtra("bundle",bundle)
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                    customStartActivity(intent)
                }
            }
            setOnChildClickListener{view, parentPos, position ->
                val item=items[position]
                if (view.id==R.id.ll_content){
                    if (item.sendStatus==2){
                        val intent= Intent(requireActivity(), ExamDetailsActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("examBean",items[parentPos])
                        intent.putExtra("bundle",bundle)
                        intent.putExtra("classPos",position)
                        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                        customStartActivity(intent)
                    }
                }
            }
        }
    }

    fun onChangeGrade(grade:Int){
        this.grade=grade
        fetchData()
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        if (grade==0)
            return
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        mPresenter.getExamList(map)
    }

}