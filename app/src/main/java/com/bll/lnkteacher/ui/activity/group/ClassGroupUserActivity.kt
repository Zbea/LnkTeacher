package com.bll.lnkteacher.ui.activity.group

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupUserPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*
import kotlinx.android.synthetic.main.common_title.*

class ClassGroupUserActivity:BaseActivity(),IContractView.IClassGroupUserView {

    private val mPresenter= ClassGroupUserPresenter(this)
    private var popWindowClass: PopupRadioList?=null
    private var index=0
    private var users= mutableListOf<ClassGroupUser>()
    private var mAdapter:ClassGroupUserAdapter?=null
    private var pops= mutableListOf<PopupBean>()
    private var position=0
    private var job=""

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        if (users.isNotEmpty()){
            this.users= users
            mAdapter?.setNewData(users)
        }
    }

    override fun onOutSuccess() {
        showToast(R.string.classgroup_kick_success)
        users.removeAt(position)
        mAdapter?.notifyDataSetChanged()
    }

    override fun onEditSuccess() {
        showToast(R.string.classgroup_set_job_success)
        users[position].job=job
        mAdapter?.notifyDataSetChanged()
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        index=intent.flags
        pops= DataBeanManager.popClassGroups
        mPresenter.getClassList(pops[index].id)
    }

    override fun initView() {
        setPageTitle(R.string.details)
        showView(tv_class)

        tv_class.text= DataBeanManager.classGroups[index].name

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            if (view.id==R.id.tv_out){
                outDialog()
            }
            if (view.id==R.id.tv_job){
                setChangeJob()
            }
        }

        tv_class.setOnClickListener {
            selectorClassGroupView()
        }

    }

    /**
     * 踢出学生
     */
    private fun outDialog(){
        CommonDialog(this).setContent(R.string.classgroup_is_kick_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                mPresenter.outClassUser(users[position].id)
            }
        })
    }

    /**
     * 休息学生职务
     */
    private fun setChangeJob(){
        InputContentDialog(this,getString(R.string.classgroup_set_job)).builder().setOnDialogClickListener{
            job=it
            mPresenter.editUser(users[position].id,job)
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
                mPresenter.getClassList(item.id)
            }
        }
        else{
            popWindowClass?.show()
        }
    }



}