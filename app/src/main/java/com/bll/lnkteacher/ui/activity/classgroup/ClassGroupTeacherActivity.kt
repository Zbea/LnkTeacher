package com.bll.lnkteacher.ui.activity.classgroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupTeacher
import com.bll.lnkteacher.mvp.presenter.ClassGroupTeacherPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupTeacherAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.rv_list
import org.greenrobot.eventbus.EventBus

class ClassGroupTeacherActivity:BaseAppCompatActivity(),IContractView.IClassGroupTeacherView {

    private lateinit var mPresenter:ClassGroupTeacherPresenter
    private var mClassGroup: ClassGroup? = null
    private var users= mutableListOf<ClassGroupTeacher>()
    private var mAdapter:ClassGroupTeacherAdapter?=null
    private var position=0
    private var isCreate=false

    override fun onUserList(users: MutableList<ClassGroupTeacher>) {
        this.users= users
        mAdapter?.setNewData(users)
    }

    override fun onOutSuccess() {
        users.removeAt(position)
        mAdapter?.notifyDataSetChanged()
    }

    override fun onTransferSuccess() {
        showToast("转让班主任成功")
        isCreate=false
        mAdapter?.setChange(false)
        mPresenter.getClassList(mClassGroup?.classGroupId!!)
        EventBus.getDefault().post(Constants.CLASSGROUP_INFO_EVENT)
        EventBus.getDefault().post(Constants.CLASSGROUP_INFO_CHANGE_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_teacher
    }

    override fun initData() {
        initChangeScreenData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        isCreate=mClassGroup?.userId==mUserId
        mPresenter.getClassList(mClassGroup?.classGroupId!!)
    }

    override fun initChangeScreenData() {
        mPresenter= ClassGroupTeacherPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("${mClassGroup?.name}  教师详情")

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupTeacherAdapter(R.layout.item_classgroup_teacher, isCreate,users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            when (view.id) {
                R.id.tv_out -> {
                    outDialog()
                }
                R.id.tv_transfer->{
                    transferDialog()
                }
            }
        }
    }

    private fun transferDialog(){
        CommonDialog(this).setContent("该教师接管班主任？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val map=HashMap<String,Any>()
                map["id"]=mClassGroup?.classGroupId!!
                map["userId"]=users[position].userId
                mPresenter.transfer(map)
            }
        })
    }

    private fun outDialog(){
        CommonDialog(this).setContent("确认踢出该老师？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val map=HashMap<String,Any>()
                map["id"]=users[position].id
                map["userId"]=users[position].userId
                map["classGroupId"]=mClassGroup?.classGroupId!!
                mPresenter.outTeacher(map)
            }
        })
    }

    override fun onRefreshData() {
        mPresenter.getClassList(mClassGroup?.classGroupId!!)
    }

}