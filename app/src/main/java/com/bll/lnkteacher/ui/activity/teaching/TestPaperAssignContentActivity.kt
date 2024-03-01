package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.DateTimeSelectorDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupCheckList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import com.bll.lnkteacher.utils.DateUtils
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.*

class TestPaperAssignContentActivity : BaseActivity(),IContractView.ITestPaperAssignView {

    private val presenter=TestPaperAssignPresenter(this)
    private var mAdapter: TestPaperAssignContentAdapter? = null
    private var typeBean:TypeBean?=null
    private var items = mutableListOf<ContentListBean>()
    private var popGroups= mutableListOf<PopupBean>()
    private var classSelectGroups= mutableListOf<PopupBean>()
    private var grade=0
    private var commitTime=0L

    override fun onType(typeList: TypeList?) {
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
        showToast(R.string.delete_success)
        items.removeAll(getCheckedItems())
        mAdapter?.setNewData(items)
    }
    override fun onSendSuccess() {
        showToast(R.string.teaching_assign_success)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        pageSize=12
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("type") as TypeBean
        grade=typeBean?.grade!!
        fetchData()

        for (item in DataBeanManager.classGroups){
            if (item.state==1&&item.grade==grade){
                popGroups.add(PopupBean(item.classId,item.name,false))
            }
        }
    }

    override fun initView() {
        setPageTitle(typeBean?.name.toString())

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TestPaperAssignContentAdapter(R.layout.item_testpaper_assign_content, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                presenter.getPaperImages(items[position].taskId)
            }
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.cb_check){
                    for (item in items){
                        item.isCheck=false
                    }
                    val item =items[position]
                    item.isCheck=true
                    notifyDataSetChanged()
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                CommonDialog(this@TestPaperAssignContentActivity).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object :
                    CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        val item=items[position]
                        val ids= arrayListOf(item.taskId)
                        presenter.deletePapers(ids)
                    }
                })
                true
            }
        }

        tv_time.setOnClickListener {
            DateTimeSelectorDialog(this).builder().setOnDateListener { timeStr, timeLong ->
                tv_time.text=DateUtils.longToStringNoYear(timeLong)
                commitTime=timeLong
            }
        }

        tv_group.setOnClickListener {
            selectorGroup()
        }

        tv_commit.setOnClickListener {
            if (classSelectGroups.size==0){
                showToast("未选择班级")
                return@setOnClickListener
            }
            if (getCheckedItems().size==0){
                showToast("未选择试卷")
                return@setOnClickListener
            }
            if (commitTime<=System.currentTimeMillis()){
                showToast("提交时间错误")
                return@setOnClickListener
            }

            val ids= mutableListOf<Int>()
            for (item in getCheckedItems()){
                ids.add(item.taskId)
            }

            val classIds= mutableListOf<Int>()
            for (item in classSelectGroups){
                classIds.add(item.id)
            }

            val map=HashMap<String,Any>()
            map["type"]=1
            map["ids"]=ids.toIntArray()
            map["classIds"]=classIds.toIntArray()
            map["grade"]=typeBean?.grade!!
            map["endTime"]=commitTime
            presenter.sendPapers(map)
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

    private fun selectorGroup() {
        PopupCheckList(this, popGroups, tv_group,  5).builder()?.setOnSelectListener{
            classSelectGroups= it as MutableList<PopupBean>
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"] = typeBean?.id!!
        presenter.getPaperList(map)
    }

}