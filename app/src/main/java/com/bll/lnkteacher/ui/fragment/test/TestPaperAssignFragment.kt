package com.bll.lnkteacher.ui.fragment.test

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAssignContentActivity
import com.bll.lnkteacher.ui.adapter.TestPaperAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class TestPaperAssignFragment:BaseMainFragment(),IContractView.ITestPaperAssignView {

    private val presenter=TestPaperAssignPresenter(this)
    private var mAdapter: TestPaperAssignAdapter?=null
    private var types= mutableListOf<TypeBean>()
    private var addTypeStr=""

    override fun onType(typeList: TypeList) {
        setPageNumber(typeList.total)
        types=typeList.list
        mAdapter?.setNewData(types)
    }
    override fun onTypeSuccess() {
        if (types.size==pageSize){
            pageIndex+=1
        }
        fetchData()
    }
    override fun onList(assignContent: AssignContent?) {
    }
    override fun onImageList(lists: MutableList<ContentListBean>?) {
    }
    override fun onDeleteSuccess() {
    }
    override fun onSendSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=6
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
            DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,60f),
            DP2PX.dip2px(activity,30f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,2)

        mAdapter = TestPaperAssignAdapter(R.layout.item_testpaper_assign,types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(2,DP2PX.dip2px(activity,50f)))
            setOnItemClickListener  { _, _, position ->
                val intent=Intent(activity, TestPaperAssignContentActivity::class.java)
                val bundle= Bundle()
                bundle.putSerializable("type",types[position])
                intent.putExtra("bundle",bundle)
                startActivity(intent)
            }
        }
    }


    /**
     * 添加考卷
     */
    fun addType(name: String){
        addTypeStr=name
        presenter.addType(name,grade)
    }

    /**
     * 刷新年级
     */
    fun changeGrade(grade:Int){
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
        map["type"]=1
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        presenter.getType(map)
    }

}