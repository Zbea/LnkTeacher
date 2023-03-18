package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.HomeworkPublishClassGroupSelectDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.mvp.model.homework.HomeworkType
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import kotlinx.android.synthetic.main.ac_homework_assgin_content.*

class HomeworkAssignContentActivity:BaseActivity(),IContractView.IHomeworkAssignView {

    private val mPresenter=HomeworkAssignPresenter(this)
    private var mAdapter:TestPaperAssignContentAdapter?=null
    private var items= mutableListOf<ContentListBean>()//列表数据
    private var homeTypeBean: HomeworkType.TypeBean?=null//作业卷分类
    private var selectDialog:HomeworkPublishClassGroupSelectDialog?=null
    private var selectClasss= mutableListOf<HomeworkClass>()//选中班级

    override fun onTypeList(homeworkType: HomeworkType?) {
    }
    override fun onAddSuccess() {
    }
    override fun onList(homeworkContent: AssignContent) {
        setPageNumber(homeworkContent.total)
        items= homeworkContent.list
        mAdapter?.setNewData(items)
    }
    override fun onImageList(lists: MutableList<ContentListBean>?) {
        val images= mutableListOf<String>()
        if (lists.isNullOrEmpty())return
        for (item in lists){
            images.add(item.url)
        }
        ImageDialog(this,images).builder()
    }


    override fun layoutId(): Int {
        return R.layout.ac_homework_assgin_content
    }

    override fun initData() {
        pageSize=12
        homeTypeBean= intent.getBundleExtra("bundle").getSerializable("homeworkType") as HomeworkType.TypeBean
        fetchData()
    }

    override fun initView() {
        setPageTitle(homeTypeBean?.name!!)

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
                    mPresenter.getImages(items[position].taskId)
                }
            }
        }

        tv_class_name.setOnClickListener {
            if (selectDialog==null){
                selectDialog=HomeworkPublishClassGroupSelectDialog(this).builder()
                selectDialog?.setOnDialogClickListener {
                    selectClasss= it
                }
            }
            else{
                selectDialog?.show()
            }
        }

        tv_send.setOnClickListener {
            if (getCheckedItems().isNotEmpty()){
                if (selectClasss.isEmpty())
                {

                }
                else{
                    showToast("未选中班级")
                }
            }
            else{
                showToast("未选中作业卷")
            }
        }

    }


    /**
     * 获的选中作业卷
     */
    private fun getCheckedItems():MutableList<ContentListBean>{
        val lists= mutableListOf<ContentListBean>()
        for (item in items){
            if (item.isCheck)
                lists.add(item)
        }
        return lists
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"] = homeTypeBean?.taskId!!
        mPresenter.getList(map)
    }

}