package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.HomeworkPublishClassGroupSelectDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelect
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.presenter.HomeworkPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TestPaperAssignContentAdapter
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.*

class HomeworkAssignContentActivity:BaseActivity(),IContractView.IHomeworkPaperAssignView {

    private var grade=0
    private val mPresenter= HomeworkPaperAssignPresenter(this)
    private var mAdapter:TestPaperAssignContentAdapter?=null
    private var items= mutableListOf<ContentListBean>()//列表数据
    private var typeBean: TypeBean?=null//作业卷分类
    private var selectDialog:HomeworkPublishClassGroupSelectDialog?=null
    private var selectClasss= mutableListOf<HomeworkClass>()//选中班级
    private var position=0

    override fun onList(homeworkContent: AssignContent) {
        setPageNumber(homeworkContent.total)
        items= homeworkContent.list
        mAdapter?.setNewData(items)
    }
    override fun onImageList(lists: MutableList<ContentListBean>) {
        val images= mutableListOf<String>()
        for (item in lists){
            images.add(item.url)
        }
        ImageDialog(this,images).builder()
    }
    override fun onCommitSuccess() {
        MethodManager.saveCommitClass(typeBean!!.id,selectClasss)
        showToast(R.string.teaching_assign_success)
        finish()
    }

    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        mAdapter?.remove(position)
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        pageSize=12
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("homeworkType") as TypeBean
        grade=typeBean?.grade!!
        selectClasss=MethodManager.getCommitClass(typeBean?.id!!)
        fetchData()
    }

    override fun initView() {
        setPageTitle(typeBean?.name!!)
        disMissView(tv_time)

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TestPaperAssignContentAdapter(R.layout.item_testpaper_assign_content, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                mPresenter.getImages(items[position].taskId)
            }
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.cb_check){
                    for (item in items){
                        if (item.isCheck)
                            item.isCheck=false
                    }
                    val item =items[position]
                    item.isCheck=!item.isCheck
                    notifyDataSetChanged()
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@HomeworkAssignContentActivity.position=position
                delete()
                true
            }
        }

        tv_group.setOnClickListener {
            if (selectDialog==null){
                selectDialog=HomeworkPublishClassGroupSelectDialog(this, grade, typeBean?.id!!).builder()
                selectDialog?.setOnDialogClickListener {
                    selectClasss= it
                }
            }
            else{
                selectDialog?.show()
            }
        }

        tv_commit.setOnClickListener {
            if (getCheckedItems().isNotEmpty()&& selectClasss.isNotEmpty()){
                commitHomework(getCheckedItems()[0].title,selectClasss)
            }
        }

    }

    private fun delete(){
        CommonDialog(this).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[position]
                val ids= arrayListOf(item.taskId)
                mPresenter.deletePapers(ids)
            }
        })
    }

    /**
     * 发送作业本消息
     */
     private fun commitHomework(contentStr:String,homeClasss:List<HomeworkClass>){
        val selects= mutableListOf<HomeworkClassSelect>()
        var isCommit=false
        for (ite in homeClasss){
            if (ite.date>0)
                isCommit=true
            selects.add(HomeworkClassSelect().apply {
                classId=ite.classId
                submitStatus=if (ite.isCommit) 0 else 1
                endTime=ite.date/1000
            })
        }

        val ids= mutableListOf<Int>()
        for (item in getCheckedItems()){
            ids.add(item.taskId)
        }

        val map=HashMap<String,Any>()
        map["subType"]=typeBean?.subType!!
        map["title"]=contentStr
        map["examList"]=selects
        map["name"]=typeBean?.name!!
        map["ids"]=ids.toIntArray()
        map["grade"]=typeBean?.grade!!
        map["showStatus"]=if (isCommit) 0 else 1
        mPresenter.commitHomeworkReel(map)
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
        map["name"]=typeBean?.name.toString()
        map["grade"] = typeBean?.grade!!
        mPresenter.getList(map)
    }

}