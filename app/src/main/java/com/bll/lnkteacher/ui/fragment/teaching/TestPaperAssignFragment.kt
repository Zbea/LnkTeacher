package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.TestPaper
import com.bll.lnkteacher.mvp.model.TestPaperType.TypeBean
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAssignContentActivity
import com.bll.lnkteacher.ui.adapter.TestPaperAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class TestPaperAssignFragment:BaseFragment(),IContractView.ITestPaperAssignView {

    private val presenter=TestPaperAssignPresenter(this)
    private var mAdapter: TestPaperAssignAdapter?=null
    private var types= mutableListOf<TypeBean>()
    private var addTypeStr=""

    override fun onType(types: MutableList<TypeBean>?) {
        this.types=types!!
        mAdapter?.setNewData(types)
    }
    override fun onTypeSuccess() {
        types.add(TypeBean().apply {
            id=types.size+1
            name=addTypeStr
        })
        mAdapter?.notifyDataSetChanged()
    }
    override fun onList(testPaper: TestPaper?) {
    }
    override fun onImageList(testPaper: TestPaper?) {
    }
    override fun onDeleteSuccess() {
    }
    override fun onGroupTypes() {
    }
    override fun onSendSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        disMissView(ll_page_number)
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,50f),
            DP2PX.dip2px(activity,30f),DP2PX.dip2px(activity,30f))
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,2)

        mAdapter = TestPaperAssignAdapter(R.layout.item_testpaper_assign,types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,80))
            setOnItemClickListener  { _, _, position ->
                startActivity(Intent(activity, TestPaperAssignContentActivity::class.java)
                    .putExtra("title",types[position].name).setFlags(types[position].id)
                )
            }
        }
    }

    //????????????????????????
    private fun fetchData(){
        presenter.getType()
    }

    /**
     * ????????????
     */
    fun addType(name: String){
        addTypeStr=name
        presenter.addType(name)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            fetchData()
        }
    }

}