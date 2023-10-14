package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.HomeworkAssignDetailsDialog
import com.bll.lnkteacher.dialog.HomeworkPublishDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelect
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.HomeworkAssignContentActivity
import com.bll.lnkteacher.ui.adapter.HomeworkAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class HomeworkAssignFragment:BaseFragment(),IContractView.IHomeworkAssignView {

    private val mPresenter=HomeworkAssignPresenter(this)
    private var mAdapter:HomeworkAssignAdapter?=null
    private var types= mutableListOf<TypeBean>()
    private var position=0
    private var detailsDialog:HomeworkAssignDetailsDialog?=null
    private var grade=0

    override fun onTypeList(list:  TypeList) {
        setPageNumber(list.total)
        types=list.list
        mAdapter?.setNewData(types)
    }
    override fun onAddSuccess() {
        if (types.size==pageSize){
            pageIndex+=1
        }
        fetchData()
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        val typeBean=types[position]
        val homeworkBook=BookGreenDaoManager.getInstance().queryTextBookByBookID(6,typeBean.bookId)
        if (homeworkBook!=null)
        {
            homeworkBook.isHomework=false
            BookGreenDaoManager.getInstance().insertOrReplaceBook(homeworkBook)
        }
        mAdapter?.remove(position)
    }
    override fun onList(homeworkContent: AssignContent?) {
    }
    override fun onImageList(lists: MutableList<ContentListBean>?) {
    }
    override fun onCommitSuccess() {
        showToast(R.string.teaching_assign_success)
    }

    override fun onDetails(details: HomeworkAssignDetails) {
        detailsDialog=HomeworkAssignDetailsDialog(requireContext(), details.list).builder()
        detailsDialog?.setDialogClickListener {
            mPresenter.deleteDetails(it)
        }

    }
    override fun onDetailsDeleteSuccess() {
        showToast(R.string.delete_success)
        detailsDialog?.refreshList()
        detailsDialog?.dismiss()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=9

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,60f),
            DP2PX.dip2px(activity,40f), 0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)

        mAdapter = HomeworkAssignAdapter(R.layout.item_homework_assign, types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3,DP2PX.dip2px(requireActivity(),50f)))
            setOnItemClickListener { _, _, position ->
                this@HomeworkAssignFragment.position=position
                val item=types[position]
                if(item.subType==1){
                    val intent=Intent(activity, HomeworkAssignContentActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("homeworkType",item)
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                else{
                    HomeworkPublishDialog(requireContext(),grade).builder().setOnDialogClickListener{
                            contentStr,homeClasss->
                        commitHomework(item,contentStr,homeClasss)
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@HomeworkAssignFragment.position=position
                deleteHomeworkBook(types[position])
                true
            }
        }
    }

    override fun lazyLoad() {
        fetchData()
    }

    /**
     * 删除题卷本
     */
    private fun deleteHomeworkBook(item: TypeBean){
        CommonDialog(requireActivity()).setContent(R.string.toast_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    if (item.subType==4){
                        val map=HashMap<String,Any>()
                        map["id"]=item.id
                        map["bookId"]=item.bookId
                        mPresenter.deleteType(map)
                    }
                    else{
                        val map=HashMap<String,Any>()
                        map["ids"]= arrayOf(item.id)
                        mPresenter.deleteHomeworkType(map)
                    }
                }
            })
    }

    /**
     * 发送作业本消息
     */
    private fun commitHomework(item:TypeBean, contentStr:String,homeClasss:List<HomeworkClass>){
        val selects= mutableListOf<HomeworkClassSelect>()
        var isCommit=false
        for (ite in homeClasss){
            if (ite.date>0)
                isCommit=true
            selects.add(HomeworkClassSelect().apply {
                classId=ite.classId
                submitStatus=ite.submitStatus
                endTime=ite.date/1000
            })
        }
        val map=HashMap<String,Any>()
        map["subType"]=item.subType
        map["title"]=contentStr
        map["examList"]=selects
        map["name"]=item.name
        map["grade"]=grade
        map["showStatus"]=if (isCommit) 0 else 1
        map["commonTypeId"]=item.id
        mPresenter.commitHomework(map)
    }

    /**
     * 添加作业本
     */
    fun addHomeworkType(item: TypeBean, subType:Int){
        val map=HashMap<String,Any>()
        map["name"]=item.name
        map["type"]=2
        map["subType"]=subType
        map["grade"]=grade
        map["bgResId"]=DataBeanManager.getHomeworkCoverStr()
        mPresenter.addType(map)
    }

    /**
     * 刷新年级
     */
    fun changeGrade(grade:Int){
        this.grade=grade
        fetchData()
    }

    /**
     * 布置详情
     */
    fun showAssignDetails(){
        mPresenter.onDetails(grade)
    }

    override fun onRefreshData() {
       fetchData()
    }

    override fun fetchData() {
        if (grade==0)
            return
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        map["type"]=2
        mPresenter.getTypeList(map)
    }
}