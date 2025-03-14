package com.bll.lnkteacher.ui.activity.classgroup

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupUserPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupUserAdapter
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.ac_classgroup_user.iv_arrow_page_down
import kotlinx.android.synthetic.main.ac_classgroup_user.iv_arrow_page_up
import kotlinx.android.synthetic.main.ac_classgroup_user.rv_list
import kotlinx.android.synthetic.main.common_title.tv_custom
import kotlinx.android.synthetic.main.common_title.tv_custom_1
import org.greenrobot.eventbus.EventBus

class ClassGroupUserActivity : BaseAppCompatActivity(), IContractView.IClassGroupUserView {

    private lateinit var mPresenter :ClassGroupUserPresenter
    private var mClassGroup: ClassGroup? = null
    private var users = mutableListOf<ClassGroupUser>()
    private var mAdapter: ClassGroupUserAdapter? = null
    private var position = 0
    private var job = ""
    private var isCreate = false

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        this.users = users
        mAdapter?.setNewData(users)
    }

    override fun onOutSuccess() {
        showToast(R.string.classgroup_kick_success)
        mAdapter?.remove(position)
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

    override fun onEditSuccess() {
        showToast(R.string.classgroup_set_job_success)
        users[position].job = job
        mAdapter?.notifyDataSetChanged()
    }

    override fun onAllowSuccess() {
        if (mClassGroup?.isAllowJoin==1){
            mClassGroup?.isAllowJoin=2
            setPageCustom("打开班群")
        }
        else{
            mClassGroup?.isAllowJoin=1
            setPageCustom("关闭班群")
        }
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        initChangeScreenData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        isCreate = mClassGroup?.userId == mUserId
        mPresenter.getClassList(mClassGroup!!.classId)
    }

    override fun initChangeScreenData() {
        mPresenter = ClassGroupUserPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("${mClassGroup?.name}  详情")
        if (mClassGroup?.state==1){
            setPageSetting("教师详情")
            if (mClassGroup?.userId==mUserId)
                setPageCustom(if (mClassGroup?.isAllowJoin==1) "关闭班群" else "开启班群")
        }

        tv_custom_1.setOnClickListener {
            val intent = Intent(this, ClassGroupTeacherActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", mClassGroup)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

        tv_custom.setOnClickListener {
            val titleInfo=if (mClassGroup?.isAllowJoin==1) "不允许学生加入班群？" else "允许学生加入班群？"
            CommonDialog(this).setContent(titleInfo).builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    mPresenter.allowJoinGroup(mClassGroup?.classId!!,if (mClassGroup?.isAllowJoin==1) 2 else 1)
                }
            }
        }

        iv_arrow_page_up.setOnClickListener {
            rv_list.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_arrow_page_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this,100f))
        }

        initRecyclerView()
    }


    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, isCreate, users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            when (view.id) {
                R.id.tv_out -> {
                    outDialog()
                }
                R.id.tv_job -> {
                    if (mClassGroup?.state==1&&isCreate)
                        setChangeJob()
                }
            }
        }
    }



    /**
     * 踢出学生
     */
    private fun outDialog() {
        val titleStr="确认踢出${users[position].nickname}同学?"
        CommonDialog(this).setContent(titleStr).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                mPresenter.outClassUser(mClassGroup?.classId!!,mClassGroup?.classGroupId!!,users[position].studentId)
            }
        })
    }

    /**
     * 休息学生职务
     */
    private fun setChangeJob() {
        InputContentDialog(this, getString(R.string.classgroup_set_job)).builder().setOnDialogClickListener {
            job = it
            mPresenter.editUser(users[position].id,mClassGroup?.classId!!, job)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.CLASSGROUP_CHANGE_EVENT){
            isCreate=false
            mAdapter?.setChange(false)
        }
    }

    override fun onRefreshData() {
        mPresenter.getClassList(mClassGroup!!.classId)
    }

}