package com.bll.lnkteacher.ui.activity.group

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.presenter.GroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.GroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*
import kotlinx.android.synthetic.main.common_title.*

class GroupUserActivity:BaseActivity(),IContractView.IGroupView {

    private val mPresenter= GroupPresenter(this)
    private var users= mutableListOf<GroupUser>()
    private var mAdapter: GroupUserAdapter?=null
    private var pops= mutableListOf<PopupBean>()
    private var position=0

    override fun onCreateGroupSuccess() {
    }
    override fun onAddSuccess() {
    }
    override fun onQuitSuccess() {
    }
    override fun getGroupUser(lists: MutableList<GroupUser>?) {
        users=lists!!
        mAdapter?.setNewData(users)
    }

    override fun layoutId(): Int {
        return R.layout.ac_group_user
    }

    override fun initData() {
        position=intent.getIntExtra("position",0)
        val id=intent.getIntExtra("id",0)

        val groups=DataBeanManager.schoolGroups

        for (i in 0 until groups.size){
            val item=groups[i]
            pops.add(PopupBean(item.id, item.schoolName, i == position))
        }

        mPresenter.getGroupUsers(id)

    }

    override fun initView() {
        setPageTitle(R.string.details)

        if (pops.size>0){
            tv_class.text= pops[position].name
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = GroupUserAdapter(R.layout.item_group_user, users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)

        tv_class.setOnClickListener {
            PopupRadioList(this, pops, tv_class, tv_class.width, 5).builder().setOnSelectListener { item ->
                tv_class.text = item.name
                mPresenter.getGroupUsers(item.id)
            }
        }

    }
}