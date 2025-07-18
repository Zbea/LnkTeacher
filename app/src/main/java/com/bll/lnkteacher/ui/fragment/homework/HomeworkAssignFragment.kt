package com.bll.lnkteacher.ui.fragment.homework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ClassGroupSelectorDialog
import com.bll.lnkteacher.dialog.HomeworkAssignDetailsDialog
import com.bll.lnkteacher.dialog.HomeworkPublishDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignItem
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.HomeworkAssignContentActivity
import com.bll.lnkteacher.ui.activity.teaching.HomeworkDrawContentTypeActivity
import com.bll.lnkteacher.ui.adapter.HomeworkAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class HomeworkAssignFragment:BaseFragment(),IContractView.IHomeworkAssignView {

    private val mPresenter=HomeworkAssignPresenter(this)
    private var mAdapter:HomeworkAssignAdapter?=null
    private var editTypeStr=""
    private var types= mutableListOf<TypeBean>()
    private var position=0
    private var detailsDialog:HomeworkAssignDetailsDialog?=null
    private var ids=""

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
        val homeworkBook=TextbookGreenDaoManager.getInstance().queryTextBookByBookId(1,typeBean.bookId)
        if (homeworkBook!=null)
        {
            homeworkBook.isHomework=false
            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(homeworkBook)
        }
        mAdapter?.remove(position)
    }

    override fun onEditSuccess() {
        types[position].name=editTypeStr
        mAdapter?.notifyItemChanged(position)
    }

    override fun onTopSuccess() {
        pageIndex=1
        fetchData()
    }

    override fun onBingSuccess() {
        showToast("修改绑定班群成功")
        types[position].classIds=ids
    }

    override fun onCommitSuccess() {
        showToast(R.string.teaching_assign_success)
    }

    override fun onDetails(details: HomeworkAssignDetailsList) {
        detailsDialog=HomeworkAssignDetailsDialog(requireContext(), details.list).builder()
        detailsDialog?.setDialogClickListener {
            mPresenter.deleteDetail(it)
        }

    }
    override fun onDetailsDeleteSuccess() {
        showToast(R.string.delete_success)
        detailsDialog?.refreshList()
        detailsDialog?.dismiss()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=9

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
            DP2PX.dip2px(activity,30f), 0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)

        mAdapter = HomeworkAssignAdapter(R.layout.item_homework_assign, types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceGridItemDeco(3, DP2PX.dip2px(requireActivity(),60f)))
            setOnItemClickListener { _, _, position ->
                this@HomeworkAssignFragment.position=position
                val item=types[position]
                when(item.subType){
                    1->{
                        val intent= Intent(activity, HomeworkAssignContentActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("homeworkType",item)
                        intent.putExtra("bundle",bundle)
                        customStartActivity(intent)
                    }
                    7->{
                        val intent= Intent(activity, HomeworkDrawContentTypeActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("homeworkType",item)
                        intent.putExtra("bundle",bundle)
                        customStartActivity(intent)
                    }
                    else->{
                        HomeworkPublishDialog(requireContext(),item).builder().setOnDialogClickListener{
                            commitHomework(item,it)
                        }
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@HomeworkAssignFragment.position=position
                if (types[position].addType==0){
                    onLongClick()
                }
                true
            }
        }
    }

    private fun onLongClick(){
        val item=types[position]
        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = "删除"
            resId = R.mipmap.icon_setting_delete
        })
        beans.add(ItemList().apply {
            name = "重命名"
            resId = R.mipmap.icon_setting_edit
        })
        beans.add(ItemList().apply {
            name = "绑定管理"
            resId = R.mipmap.icon_setting_set
        })

        LongClickManageDialog(requireActivity(),2, item.name, beans).builder()
            .setOnDialogClickListener { position->
                when(position){
                    0->{
                        if (item.subType==4){
                            val map=HashMap<String,Any>()
                            map["id"]=item.id
                            map["bookId"]=item.bookId
                            mPresenter.deleteHomeworkBookType(map)
                        }
                        else{
                            val map=HashMap<String,Any>()
                            map["ids"]= arrayOf(item.id)
                            mPresenter.deleteHomeworkType(map)
                        }
                    }
                    1->{
                        InputContentDialog(requireActivity(),2,item.name).builder().setOnDialogClickListener{
                            editTypeStr=it
                            val map=HashMap<String,Any>()
                            map["id"]= item.id
                            map["name"]= it
                            mPresenter.editHomeworkType(map)
                        }
                    }
//                    2->{
//                        val map=HashMap<String,Any>()
//                        map["id"]=item.id
//                        mPresenter.topType(map)
//                    }
                    2->{
                        val classIds=item.classIds.split(",")
                        val items= mutableListOf<ClassGroup>()
                        for (classGroup in DataBeanManager.getClassGroups(grade)){
                            if (classIds.isNotEmpty() &&classIds.contains(classGroup.classId.toString()))
                                classGroup.isCheck=true
                            items.add(classGroup)
                        }
                        ClassGroupSelectorDialog(requireActivity(),2,items).builder().setOnDialogSelectListener{
                            ids=ToolUtils.getImagesStr(it)
                            val map=HashMap<String,Any>()
                            map["id"]=item.id
                            map["classIds"]=ids
                            mPresenter.bingType(map)
                        }
                    }
                }
            }
    }


    /**
     * 发送作业本消息
     */
    private fun commitHomework(item:TypeBean,classSelect: HomeworkAssignItem){
        val map=HashMap<String,Any>()
        map["title"]=classSelect.contentStr
        map["classIds"]=classSelect.classIds
        map["showStatus"]=classSelect.showStatus
        map["endTime"]=if (classSelect.showStatus==0) classSelect.endTime/1000 else 0
        map["commonTypeId"]=item.id
        mPresenter.commitHomework(map)
    }

    /**
     * 添加作业本
     */
    fun addHomeworkType(item: TypeBean){
        val map=HashMap<String,Any>()
        map["name"]=item.name
        map["type"]=2
        map["subType"]=item.subType
        map["grade"]=grade
        map["classIds"]=item.classIds
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
        mPresenter.getDetailsList(grade)
    }

    override fun onRefreshData() {
       fetchData()
    }

    override fun fetchData() {
        if (NetworkUtil.isNetworkConnected()&&grade>0) {
            val map = HashMap<String, Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"] = grade
            map["type"] = 2
            mPresenter.getTypeList(map)
        }
    }

}