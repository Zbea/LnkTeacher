package com.bll.lnkteacher.ui.fragment.group

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.GroupAddDialog
import com.bll.lnkteacher.dialog.GroupCreateDialog
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.presenter.GroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.group.GroupUserActivity
import com.bll.lnkteacher.ui.adapter.GroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class GroupFragment:BaseFragment(),IContractView.IGroupView{

    private val groupPresenter= GroupPresenter(this)
    private var groups= mutableListOf<Group>()
    private var mAdapter: GroupAdapter?=null
    private var position=0

    override fun onCreateGroupSuccess() {
        mCommonPresenter.getGroups()
    }

    override fun onAddSuccess() {
        mCommonPresenter.getGroups()
    }

    override fun onQuitSuccess() {
        showToast(if (groups[position].selfStatus==2) R.string.out_success else R.string.dissolve_success)
        mAdapter?.remove(position)
        DataBeanManager.schoolGroups=groups
    }
    override fun getGroupUser(users: MutableList<GroupUser>?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        mAdapter= GroupAdapter(R.layout.item_group,groups)
        rv_list.layoutManager = LinearLayoutManager(context)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0, DP2PX.dip2px(activity,40f)))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            when (view.id) {
                R.id.tv_out -> {
                    CommonDialog(requireActivity()).setContent(if (groups[position].selfStatus==2) R.string.group_is_out_tips else R.string.group_is_dissolve_tips)
                        .builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }

                            override fun ok() {
                                groupPresenter.quitGroup(groups[position].id)
                            }
                        })
                }
                R.id.tv_details -> {
                    startActivity(Intent(context,GroupUserActivity::class.java)
                        .putExtra("position",position)
                        .putExtra("id",groups[position].id)
                    )
                }
            }
        }

        onGroupEvent()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity()))
            fetchCommonData()
    }

    fun createGroup(){
        GroupCreateDialog(requireContext()).builder()?.setOnDialogClickListener{ str, grade,classIds->
            groupPresenter.createGroup(str,grade,classIds)
        }
    }

    /**
     * 加入校群
     */
    fun addGroup(){
        GroupAddDialog(requireContext()).builder()?.setOnDialogClickListener { str, classIds ->
            groupPresenter.addGroup(str, classIds)
        }
    }

    override fun onGroupEvent() {
        groups= DataBeanManager.schoolGroups
        mAdapter?.setNewData(groups)
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}