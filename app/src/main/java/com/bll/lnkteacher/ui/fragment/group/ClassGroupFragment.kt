package com.bll.lnkteacher.ui.fragment.group

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.NotebookAddDialog
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.group.ClassGroupUserActivity
import com.bll.lnkteacher.ui.adapter.ClassGroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_group.*

class ClassGroupFragment:BaseFragment(),IContractView.IClassGroupView {

    private var mGroupPresenter=ClassGroupPresenter(this)
    private var classGroups= mutableListOf<ClassGroup>()
    private var mAdapter:ClassGroupAdapter?=null
    private var position=0
    private var className=""//班群名称

    override fun onCreateSuccess() {
        showToast("创建班群成功")
        mGroupPresenter.getClassList()
    }

    override fun onClassList(list: MutableList<ClassGroup>?) {
        if (list != null) {
            classGroups=list
            mAdapter?.setNewData(classGroups)
            //更新全局班群
            DataBeanManager.getInstance().classGroups=classGroups
        }
    }

    override fun onEditSuccess() {
        showToast("修改班群名成功")
        classGroups[position].name=className
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDissolveSuccess() {
        showToast("解散班群成功")
        classGroups.removeAt(position)
        mAdapter?.notifyDataSetChanged()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_group
    }

    override fun initView() {
        initRecyclerView()

    }
    override fun lazyLoad() {
        mGroupPresenter.getClassList()
    }

    private fun initRecyclerView(){

        mAdapter=ClassGroupAdapter(R.layout.item_teaching_classgroup,classGroups)
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0,DP2PX.dip2px(activity,40f),0))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            when(view.id){
                R.id.tv_info->{
                    startActivity(Intent(activity, ClassGroupUserActivity::class.java).setFlags(position))
                }
                R.id.tv_dissolve->{
                    dissolveGroup()
                }
                R.id.tv_edit->{
                    editGroup()
                }
            }

        }

    }

    /**
     * 刷新列表
     */
    fun refreshData(){
        lazyLoad()
    }

    /**
     * 解散班群
     */
    private fun dissolveGroup(){
        CommonDialog(activity).setContent("确定解散班群？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    mGroupPresenter.dissolveClassGroup(classGroups[position].classId)
                }
            })
    }

    /**
     * 修改班群名
     */
    private fun editGroup(){
        NotebookAddDialog(requireContext(), "重命名", classGroups[position].name, "").builder()
            ?.setOnDialogClickListener {
                className=it
                mGroupPresenter.editClassGroup(classGroups[position].classId,className)
            }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            refreshData()
        }
    }

}