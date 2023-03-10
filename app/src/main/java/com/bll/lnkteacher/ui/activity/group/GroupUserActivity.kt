package com.bll.lnkteacher.ui.activity.group

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.Group
import com.bll.lnkteacher.mvp.model.GroupUser
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.GroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.GroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*
import kotlinx.android.synthetic.main.common_title.*

class GroupUserActivity:BaseActivity(),IContractView.IGroupView {

    private val mPresenter= GroupPresenter(this)
    private var popWindowClass: PopupRadioList?=null
    private var index=0//校群 班群
    private var users= mutableListOf<GroupUser>()
    private var mAdapter: GroupUserAdapter?=null
    private var pops= mutableListOf<PopupBean>()
    private var position=0

    override fun onGroupList(groups: MutableList<Group>?) {
    }
    override fun onQuitSuccess() {

    }
    override fun getGroupUser(lists: MutableList<GroupUser>?) {
        if (!lists.isNullOrEmpty()){
            users=lists
            mAdapter?.setNewData(users)
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_group_user
    }

    override fun initData() {
        index=intent.flags
        position=intent.getIntExtra("position",0)
        val id=intent.getIntExtra("id",0)

        val groups=if (index==2){
            DataBeanManager.schoolGroups
        }
        else{
            DataBeanManager.areaGroups
        }

        for (i in 0 until groups.size){
            var item=groups[i]
            pops.add(
                PopupBean(
                    item.id,
                    item.schoolName,
                    i == position
                )
            )
        }

        mPresenter.getGroupUsers(id)

    }

    override fun initView() {
        setPageTitle("详情")

        if (pops.size>0){
            tv_class.text= pops[position].name
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = GroupUserAdapter(R.layout.item_group_user, users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)


        tv_class.setOnClickListener {
            selectorClassGroupView()
        }

    }


    /**
     * 班级选择
     */
    private fun selectorClassGroupView(){
        if (popWindowClass==null){
            popWindowClass= PopupRadioList(this, pops, tv_class, tv_class.width, 5).builder()
            popWindowClass?.setOnSelectListener { item ->
                tv_class.text = item.name
                mPresenter.getGroupUsers(item.id)
            }
        }
        else{
            popWindowClass?.show()
        }
    }




}