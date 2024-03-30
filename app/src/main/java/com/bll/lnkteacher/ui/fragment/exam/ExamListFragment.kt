package com.bll.lnkteacher.ui.fragment.exam

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IExamListView
import com.bll.lnkteacher.ui.activity.exam.ExamAnalyseActivity
import com.bll.lnkteacher.ui.activity.exam.ExamCorrectActivity
import com.bll.lnkteacher.ui.adapter.ExamListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class ExamListFragment:BaseMainFragment(),IExamListView{
    private var mPresenter=ExamListPresenter(this,2)
    private var mAdapter:ExamListAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()

    override fun onList(list: ExamList) {
        items=list.list
        mAdapter?.setNewData(items)
        setPageNumber(list.total)
    }
    override fun onExamImage(url: ExamList.ExamBean?) {
    }
    override fun onExamClassUser(classUserList: ExamClassUserList?) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
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
            rv_list.addItemDecoration(SpaceItemDeco(0,0,0, DP2PX.dip2px(requireActivity(),30f)))
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
                if (view.id==R.id.ll_content){
                    val item=items[parentPos]
                    val classItem=item.classList[position]
                    val intent= Intent(requireActivity(), ExamCorrectActivity::class.java)
                    intent.putExtra("id",item.id)
                    intent.putExtra("classId",classItem.classId)
                    intent.putExtra("className",classItem.className)
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                    customStartActivity(intent)
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
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        mPresenter.getExamList(map)
    }

}