package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.TestPaper
import com.bll.lnkteacher.mvp.model.TestPaperCorrect
import com.bll.lnkteacher.mvp.model.TestPaperCorrectClass
import com.bll.lnkteacher.mvp.model.TestPaperGrade
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAnalyseActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperCorrectActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperGradeActivity
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_teaching_list.*
import kotlin.math.ceil

class TestPaperCorrectFragment:BaseFragment(),IContractView.ITestPaperCorrectView {

    private val mPresenter=TestPaperCorrectPresenter(this)
    private var mAdapter: TestPaperCorrectAdapter?=null
    private var items= mutableListOf<TestPaperCorrect.CorrectBean>()
    private var pageIndex=1 //当前页码
    private var pageCount=1
    private val pageSize=3
    private var pos=0

    override fun onList(bean: TestPaperCorrect?) {
        pageCount = ceil(bean?.total?.toDouble()!! / pageSize).toInt()
        val totalTotal = bean?.total
        if (totalTotal == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        items= bean?.list as MutableList<TestPaperCorrect.CorrectBean>
        mAdapter?.setNewData(items)
    }
    override fun onDeleteSuccess() {
        showToast("删除成功")
        mAdapter?.remove(pos)
    }
    override fun onImageList(list: MutableList<TestPaper.ListBean>?) {
    }
    override fun onClassPapers(bean: TestPaperCorrectClass?) {
    }
    override fun onGrade(list: List<TestPaperGrade>) {
    }
    override fun onCorrectSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        initRecyclerView()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                fetchData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){

        val layoutParams=LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = TestPaperCorrectAdapter(R.layout.item_testpaper_correct, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0,0,0,DP2PX.dip2px(requireActivity(),30f)))
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { _, view, position ->
                if (view.id==R.id.tv_analyse){
                    val intent=Intent(requireActivity(), TestPaperAnalyseActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("paperCorrect",items[position])
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                if (view.id==R.id.tv_data){
                    val intent=Intent(requireActivity(), TestPaperGradeActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("paperCorrect",items[position])
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                if (view.id==R.id.iv_delete){
                    this@TestPaperCorrectFragment.pos=position
                    deletePaperCorrect()
                }
            }
            setOnChildClickListener { view,parentPos, position ->
                if (view.id==R.id.ll_content){
                    val intent=Intent(requireActivity(), TestPaperCorrectActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("classBean",items[parentPos].examList[position])
                    intent.putExtra("bundle",bundle)
                    intent.flags=items[parentPos].id
                    startActivity(intent)
                }
                if (view.id==R.id.tv_save){

                }
            }
        }
    }

    private fun fetchData(){
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        mPresenter.getList(map)
    }

    /**
     * 删除批改
     */
    private fun deletePaperCorrect(){
        CommonDialog(requireActivity()).setContent("确定删除？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                mPresenter.deleteCorrect(items[pos].id)
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            fetchData()
        }
    }

}