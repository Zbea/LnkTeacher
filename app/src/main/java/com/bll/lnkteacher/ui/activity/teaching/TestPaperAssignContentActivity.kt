package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.TestPaper
import com.bll.lnkteacher.mvp.model.TestPaperType
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import kotlin.math.ceil

class TestPaperAssignContentActivity : BaseActivity(),IContractView.ITestPaperAssignView {

    private val presenter=TestPaperAssignPresenter(this)
    private var mAdapter: TestPaperAssignContentAdapter? = null
    private var id=0
    private var items = mutableListOf<TestPaper.ListBean>()
    private var popGroupNames= mutableListOf<PopupBean>()
    private var popGroups= mutableListOf<PopupBean>()
    private var pageIndex=1 //当前页码
    private var pageCount=1
    private val pageSize=12

    override fun onType(types: MutableList<TestPaperType.TypeBean>?) {
    }
    override fun onTypeSuccess() {
    }
    override fun onList(testPaper: TestPaper?) {
        pageCount = ceil(testPaper?.total?.toDouble()!! / pageSize).toInt()
        val totalTotal = testPaper?.total
        if (totalTotal == 0) {
            disMissView(ll_page_number)
        } else {
            tv_page_current.text = pageIndex.toString()
            tv_page_total.text = pageCount.toString()
            showView(ll_page_number)
        }
        items= testPaper?.list as MutableList<TestPaper.ListBean>
        mAdapter?.setNewData(items)
    }

    override fun onImageList(testPaper: TestPaper?) {
        val images= mutableListOf<String>()
        for (item in testPaper?.list!!){
            images.add(item.url)
        }
        if (images.size>0)
            ImageDialog(this,images).builder()
    }

    override fun onDeleteSuccess() {
        showToast("删除成功")
        items.removeAll(getCheckedItems())
        mAdapter?.setNewData(items)
    }


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        id=intent.flags
        fetchData()

        popGroupNames.add(PopupBean(0,"班群单考",true))
        popGroupNames.add(PopupBean(1,"校群联考",false))
        popGroupNames.add(PopupBean(2,"际群联考",false))
        popGroups=DataBeanManager.popClassGroups
        tv_group_name.text=popGroupNames[0].name
        tv_group.text=popGroups[0].name
    }

    override fun initView() {
        setPageTitle(intent.getStringExtra("title"))
        showView(tv_send,iv_manager)
        iv_manager.setImageResource(R.mipmap.icon_notebook_delete)

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TestPaperAssignContentAdapter(R.layout.item_testpaper_assign_content, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val item =items[position]
                item.isCheck=!item.isCheck
                notifyDataSetChanged()
            }
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.iv_image){
                    presenter.getPaperImages(items[position].taskId)
                }
            }
        }

        tv_group_name.setOnClickListener {
            selectorName()
        }

        tv_group.setOnClickListener {
            selectorGroup()
        }

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

        iv_manager.setOnClickListener {
            CommonDialog(this).setContent("确定删除选中？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    if (getCheckedItems().size>0){
                        val ids= mutableListOf<Int>()
                        for (item in getCheckedItems()){
                            ids.add(item.taskId)
                        }
                        presenter.deletePapers(ids)
                    }
                }
            })
        }

        tv_send.setOnClickListener {

        }

    }

    private fun fetchData(){
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"] = id
        presenter.getPaperList(map)
    }

    /**
     * 获的选中考卷
     */
    private fun getCheckedItems():MutableList<TestPaper.ListBean>{
        val lists= mutableListOf<TestPaper.ListBean>()
        for (item in items){
            if (item.isCheck)
                lists.add(item)
        }
        return lists
    }

    /**
     * 查看考卷
     */
    private fun checkImages(item: TestPaper.ListBean){
        if (!item.urls.isNullOrEmpty()){
            ImageDialog(this,item.urls).builder()
        }
    }

    private fun selectorName() {
        PopupRadioList(this, popGroupNames, tv_group_name,  5).builder()
            ?.setOnSelectListener { item ->
                tv_group_name.text = item.name
                popGroups = when(item.id){
                    0->{
                        DataBeanManager.popClassGroups
                    }
                    1->{
                        DataBeanManager.popSchoolGroups
                    }
                    else->{
                        DataBeanManager.popAreaGroups
                    }
                }
                tv_group.text=popGroups[0].name
            }
    }

    private fun selectorGroup() {
        PopupRadioList(this, popGroups, tv_group,  5).builder()
         ?.setOnSelectListener { item ->
             tv_group.text = item.name
        }
    }

}