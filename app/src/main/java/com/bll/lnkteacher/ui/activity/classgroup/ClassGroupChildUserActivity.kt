package com.bll.lnkteacher.ui.activity.classgroup

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupUserPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupChildUserAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco2
import kotlinx.android.synthetic.main.ac_list.rv_list
import org.greenrobot.eventbus.EventBus

class ClassGroupChildUserActivity : BaseAppCompatActivity(), IContractView.IClassGroupUserView {

    private lateinit var mPresenter :ClassGroupUserPresenter
    private var mClassGroup: ClassGroup? = null
    private var users = mutableListOf<ClassGroupUser>()
    private var mAdapter: ClassGroupChildUserAdapter? = null
    private var position = 0
    private var isCreate = false

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        this.users = users
        mAdapter?.setNewData(users)
    }

    override fun onOutSuccess() {
        showToast(R.string.classgroup_kick_success)
        mAdapter?.remove(position)
        EventBus.getDefault().post(Constants.CLASSGROUP_INFO_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
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

        initRecyclerView()
    }


    private fun initRecyclerView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(this,50f), DP2PX.dip2px(this,20f),
            DP2PX.dip2px(this,50f),DP2PX.dip2px(this,20f))
        rv_list.layoutParams=layoutParams


        rv_list.layoutManager = GridLayoutManager(this,2)//创建布局管理
        mAdapter = ClassGroupChildUserAdapter(R.layout.item_classgroup_child_user, isCreate, users)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            when (view.id) {
                R.id.tv_out -> {
                    outDialog()
                }
            }
        }
        rv_list.addItemDecoration(SpaceGridItemDeco2(50,0))
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


    override fun onRefreshData() {
        mPresenter.getClassList(mClassGroup!!.classId)
    }

}