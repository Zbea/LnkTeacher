package com.bll.lnkteacher.ui.fragment.group

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.group.ClassGroupUserActivity
import com.bll.lnkteacher.ui.adapter.ClassGroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_group.*
import org.greenrobot.eventbus.EventBus

class ClassGroupFragment:BaseFragment(),IContractView.IClassGroupView {

    private var mGroupPresenter=ClassGroupPresenter(this)
    private var classGroups= mutableListOf<ClassGroup>()
    private var mAdapter:ClassGroupAdapter?=null
    private var position=0

    override fun onCreateSuccess() {
        showToast(R.string.classgroup_create_success)
        mCommonPresenter.getClassGroups()
    }

    override fun onEditSuccess() {
        showToast(R.string.classgroup_edit_success)
        mCommonPresenter.getClassGroups()
    }

    override fun onDissolveSuccess() {
        showToast(R.string.classgroup_dissolve_success)
        classGroups.removeAt(position)
        mAdapter?.notifyDataSetChanged()
        DataBeanManager.classGroups=classGroups
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

    override fun onSendSuccess() {
        showToast(R.string.classgroup_send_control_success)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_group
    }

    override fun initView() {
        initRecyclerView()
        onClassGroupEvent()
    }
    override fun lazyLoad() {
        fetchCommonData()
    }

    private fun initRecyclerView(){

        mAdapter=ClassGroupAdapter(R.layout.item_group_classgroup,classGroups)
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0,DP2PX.dip2px(activity,40f)))
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
                R.id.tv_send->{
                    sendGroup()
                }
            }

        }

    }

    /**
     * 创建班群
     */
    fun createClassGroup(){
        ClassGroupCreateDialog(requireContext(),null).builder()
            .setOnDialogClickListener {name,grade,job->
                mGroupPresenter.createClassGroup(name,grade,job)
            }
    }

    /**
     * 修改班群名
     */
    private fun editGroup(){
        ClassGroupCreateDialog(requireContext(), classGroups[position]).builder()
            .setOnDialogClickListener {name,grade,job->
                mGroupPresenter.editClassGroup(classGroups[position].classId,name,job,grade)
            }
    }

    /**
     * 解散班群
     */
    private fun dissolveGroup(){
        CommonDialog(requireActivity()).setContent(R.string.classgroup_is_dissolve_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    mGroupPresenter.dissolveClassGroup(classGroups[position].classId)
                }
            })
    }

    /**
     * 老师控制学生删除
     */
    private fun sendGroup(){
        CommonDialog(requireActivity()).setContent(R.string.classgroup_is_delete_book_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    mGroupPresenter.sendClassGroupControl(classGroups[position].classId)
                }
            })
    }

    override fun onClassGroupEvent() {
        classGroups=DataBeanManager.classGroups
        mAdapter?.setNewData(classGroups)
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}