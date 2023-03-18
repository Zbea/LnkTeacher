package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperType
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.*
import kotlinx.android.synthetic.main.common_title.*

class TestPaperAssignContentActivity : BaseActivity(),IContractView.ITestPaperAssignView {

    private val presenter=TestPaperAssignPresenter(this)
    private var mAdapter: TestPaperAssignContentAdapter? = null
    private var id=0
    private var items = mutableListOf<ContentListBean>()
    private var popGroupNames= mutableListOf<PopupBean>()
    private var popGroups= mutableListOf<PopupBean>()
    private var type=1 //班群校群id
    private var subtype=0 //选中群id

    override fun onType(testPaperType: TestPaperType?) {
    }
    override fun onTypeSuccess() {
    }
    override fun onList(assignContent: AssignContent) {
        setPageNumber(assignContent.total)
        items= assignContent.list
        mAdapter?.setNewData(items)
    }
    override fun onImageList(lists: MutableList<ContentListBean>) {
        val images= mutableListOf<String>()
        for (item in lists){
            images.add(item.url)
        }
        ImageDialog(this,images).builder()
    }
    override fun onDeleteSuccess() {
        showToast("删除成功")
        items.removeAll(getCheckedItems())
        mAdapter?.setNewData(items)
    }
    override fun onGroupTypes() {
    }
    override fun onSendSuccess() {
        showToast("布置成功")
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        pageSize=12
        id=intent.flags
        fetchData()

        popGroupNames.add(PopupBean(1,"班群单考",true))
        popGroupNames.add(PopupBean(2,"校群联考",false))
        popGroupNames.add(PopupBean(3,"际群联考",false))
        tv_group_name.text=popGroupNames[0].name
        popGroups=DataBeanManager.popClassGroups
        if (popGroups.size>0){
            tv_group.text=popGroups[0].name
            subtype=popGroups[0].id
        }

        presenter.getGroupTypes()
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
            if (getCheckedItems().size>0){
                val ids= mutableListOf<Int>()
                for (item in getCheckedItems()){
                    ids.add(item.taskId)
                }
                val map=HashMap<String,Any>()
                map["type"]=type
                map["groupId"]=subtype
                map["ids"]=ids.toIntArray()
                presenter.sendPapers(map)
            }
        }
    }

    /**
     * 获的选中考卷
     */
    private fun getCheckedItems():MutableList<ContentListBean>{
        val lists= mutableListOf<ContentListBean>()
        for (item in items){
            if (item.isCheck)
                lists.add(item)
        }
        return lists
    }


    private fun selectorName() {
        PopupRadioList(this, popGroupNames, tv_group_name,  5).builder()
            ?.setOnSelectListener { item ->
                type=item.id
                tv_group_name.text = item.name
                popGroups = when(item.id){
                    1->{
                        DataBeanManager.popClassGroups
                    }
                    2->{
                        DataBeanManager.popSchoolGroups
                    }
                    else->{
                        DataBeanManager.popAreaGroups
                    }
                }
                tv_group.text=popGroups[0].name
                subtype=popGroups[0].id
            }
    }

    private fun selectorGroup() {
        PopupRadioList(this, popGroups, tv_group,  5).builder()
         ?.setOnSelectListener { item ->
             tv_group.text = item.name
             subtype=item.id
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"] = id
        presenter.getPaperList(map)
    }

}