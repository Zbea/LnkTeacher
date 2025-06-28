package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.DateTimeSelectorDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupCheckList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.presenter.TestPaperAssignContentPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_commit_time
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_group
import kotlinx.android.synthetic.main.common_title.tv_btn_1

class TestPaperAssignContentActivity : BaseAppCompatActivity(),IContractView.ITestPaperAssignContentView {

    private lateinit var presenter:TestPaperAssignContentPresenter
    private var mAdapter: TestPaperAssignContentAdapter? = null
    private var typeBean:TypeBean?=null
    private var items = mutableListOf<AssignPaperContentList.AssignPaperContentBean>()
    private var popGroups= mutableListOf<PopupBean>()
    private var grade=0
    private var position=0
    private var commitTime=0L
    private var classIds= mutableListOf<Int>()
    private var taskId=0

    override fun onList(assignPaperContentList: AssignPaperContentList) {
        setPageNumber(assignPaperContentList.total)
        items= assignPaperContentList.list
        mAdapter?.setNewData(items)
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        mAdapter?.remove(position)
    }
    override fun onSendSuccess() {
        showToast(R.string.teaching_assign_success)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("type") as TypeBean
        grade=typeBean?.grade!!

        val classSelectItem= Gson().fromJson(typeBean?.lastConfig, HomeworkAssignItem::class.java)
        if (classSelectItem!=null){
            classIds=classSelectItem.classIds
        }
        for (item in DataBeanManager.getClassGroupExcpetChilds(grade)){
            popGroups.add(PopupBean(item.classId,item.name,classIds.contains(item.classId)))
        }
        fetchData()
    }

    override fun initChangeScreenData() {
        presenter=TestPaperAssignContentPresenter(this)
    }

    override fun initView() {
        setPageTitle(typeBean?.name.toString())
        setPageOk("发送")

        tv_group.setOnClickListener {
            PopupCheckList(this, popGroups, tv_group,tv_group.width,  5).builder().setOnSelectListener{
                classIds.clear()
                for (item in it){
                    classIds.add(item.id)
                }
            }
        }

        tv_commit_time.setOnClickListener {
            DateTimeSelectorDialog(this).builder().setOnDateListener { timeStr, timeLong ->
                if (timeLong>System.currentTimeMillis()){
                    tv_commit_time.text= DateUtils.longToHour(timeLong)
                    commitTime=timeLong
                }
                else{
                    showToast("设置提交时间错误")
                }
            }
        }

        tv_btn_1.setOnClickListener {
            if (classIds.size==0){
                showToast("请选择班级")
                return@setOnClickListener
            }
            if (commitTime==0L){
                showToast("未设置提交时间")
                return@setOnClickListener
            }
            if (taskId>0)
                commit()
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TestPaperAssignContentAdapter(R.layout.item_testpaper_assign_content, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                val item =items[position]
                when (view.id) {
                    R.id.cb_check -> {
                        for (itm in items){
                            itm.isCheck=false
                        }
                        item.isCheck=true
                        taskId=item.taskId
                        notifyDataSetChanged()
                    }
                    R.id.tv_answer -> {
                        ImageDialog(this@TestPaperAssignContentActivity,item.answerUrl.split(",")).builder()
                    }
                    R.id.iv_image -> {
                        ImageDialog(this@TestPaperAssignContentActivity,item.examUrl.split(",")).builder()
                    }
                }
            }
            setOnItemChildLongClickListener { adapter, view, position ->
                this@TestPaperAssignContentActivity.position=position
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
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,60))
    }

    /**
     * 布置考卷
     */
    private fun commit(){
        val map=HashMap<String,Any>()
        map["taskId"]=taskId
        map["classIds"]=classIds
        map["endTime"]=commitTime/1000
        presenter.commitPapers(map)
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"] = typeBean?.id!!
        presenter.getTestPaperContentList(map)
    }

}