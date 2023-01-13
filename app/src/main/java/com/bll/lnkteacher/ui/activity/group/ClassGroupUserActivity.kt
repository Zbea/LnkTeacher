package com.bll.lnkteacher.ui.activity.group

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopWindowRadioList
import com.bll.lnkteacher.mvp.model.ClassGroupUser
import com.bll.lnkteacher.mvp.model.PopWindowBean
import com.bll.lnkteacher.mvp.presenter.ClassGroupUserPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*
import kotlinx.android.synthetic.main.common_title.*

class ClassGroupUserActivity:BaseActivity(),IContractView.IClassGroupUserView {

    private val mPresenter= ClassGroupUserPresenter(this)
    private var popWindowClass: PopWindowRadioList?=null
    private var index=0
    private var users= mutableListOf<ClassGroupUser>()
    private var mAdapter:ClassGroupUserAdapter?=null
    private var pops= mutableListOf<PopWindowBean>()
    private var position=0
    private var job=""

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        if (!users.isNullOrEmpty()){
            this.users= users
            mAdapter?.setNewData(users)
        }
    }

    override fun onOutSuccess() {
        showToast("踢出该学生成功")
        users.removeAt(position)
        mAdapter?.notifyDataSetChanged()
    }

    override fun onEditSuccess() {
        showToast("设置学生班级职务成功")
        users[position].job=job
        mAdapter?.notifyDataSetChanged()
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        index=intent.flags

        var datas= DataBeanManager.getIncetance().classGroups
        for (i in 0 until datas.size){
            var item=datas[i]
            pops.add(PopWindowBean(item.classId,item.name,i==index))
        }

        mPresenter.getClassList(pops[index].id)

    }

    override fun initView() {
        setPageTitle("详情")
        showView(tv_class)

        tv_class.text= DataBeanManager.getIncetance().classGroups[index].name

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
        CommonDialog(this).setContent("确认踢出该学生？").builder().setDialogClickListener(object :
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
        InputContentDialog(this,"设置职务").builder()?.setOnDialogClickListener{
            job=it
            mPresenter.editUser(users[position].id,job)
        }
    }

    /**
     * 班级选择
     */
    private fun selectorClassGroupView(){
        if (popWindowClass==null){
            popWindowClass= PopWindowRadioList(this, pops, tv_class,  5).builder()
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