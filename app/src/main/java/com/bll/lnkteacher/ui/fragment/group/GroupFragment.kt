package com.bll.lnkteacher.ui.fragment.group

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.presenter.GroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.group.GroupUserActivity
import com.bll.lnkteacher.ui.adapter.GroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment:BaseFragment(),IContractView.IGroupView{

    private val groupPresenter= GroupPresenter(this)
    private var index=0//1际群2校群
    private var groups= mutableListOf<Group>()
    private var mAdapter: GroupAdapter?=null
    private var position=0

    /**
     * 实例 传送数据
     */
    fun newInstance(index:Int):GroupFragment{
        val fragment=GroupFragment()
        val bundle=Bundle()
        bundle.putInt("index",index)
        fragment.arguments=bundle
        return fragment
    }

    override fun onGroupList(lists: MutableList<Group>?) {
        groups.clear()
        if (!lists.isNullOrEmpty()){
            for (item in lists){
                if (item.type==index){
                    groups.add(item)
                }
            }
            mAdapter?.setNewData(groups)
            //更新全局设置
            if (index==1){
                DataBeanManager.schoolGroups=groups
            }
            else{
                DataBeanManager.areaGroups=groups
            }
        }
    }
    override fun onQuitSuccess() {
        showToast(if (groups[position].selfStatus==2) "退出成功" else "解散成功")
        groups.removeAt(position)
        mAdapter?.notifyDataSetChanged()
    }
    override fun getGroupUser(users: MutableList<GroupUser>?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_group
    }

    override fun initView() {
        index= arguments?.getInt("index")!!

        mAdapter= GroupAdapter(R.layout.item_group,groups)
        rv_list.layoutManager = LinearLayoutManager(context)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0, DP2PX.dip2px(activity,40f)))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.tv_out){
                CommonDialog(requireActivity()).setContent(if (groups[position].selfStatus==2) "确定退出群？" else "确定解散群？")
                    .builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            groupPresenter.quitGroup(groups[position].id)
                        }
                    })
            }
            if (view.id==R.id.tv_details){
                startActivity(Intent(context,GroupUserActivity::class.java).addFlags(index)
                    .putExtra("position",position)
                    .putExtra("id",groups[position].id)
                )
            }
        }

    }
    override fun lazyLoad() {
        groupPresenter.getGroups(false)
    }

    /**
     * 刷新列表
     */
    fun refreshData(){
        lazyLoad()
    }

}