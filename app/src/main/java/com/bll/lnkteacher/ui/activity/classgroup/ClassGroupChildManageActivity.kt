package com.bll.lnkteacher.ui.activity.classgroup

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupChildPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupUserSelectorAdapter
import kotlinx.android.synthetic.main.ac_classgroup_child_manage.rv_list_all
import kotlinx.android.synthetic.main.ac_classgroup_child_manage.tv_add
import kotlinx.android.synthetic.main.ac_classgroup_child_manage.tv_class_child_name
import kotlinx.android.synthetic.main.ac_classgroup_child_manage.tv_class_name
import kotlinx.android.synthetic.main.ac_classgroup_child_manage.tv_out
import kotlinx.android.synthetic.main.ac_list.rv_list

class ClassGroupChildManageActivity : BaseAppCompatActivity(), IContractView.IClassGroupChildView {

    private lateinit var mPresenter :ClassGroupChildPresenter
    private var mClassGroup: ClassGroup? = null
    private var allUsers = mutableListOf<ClassGroupUser>()
    private var currentUsers = mutableListOf<ClassGroupUser>()
    private var mCurrentAdapter: ClassGroupUserSelectorAdapter? = null
    private var mAllAdapter: ClassGroupUserSelectorAdapter? = null

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        allUsers = users
        mPresenter.getClassChildUser(mClassGroup!!.classId)
    }

    override fun onChildUserList(users: MutableList<ClassGroupUser>) {
        currentUsers=users
        mCurrentAdapter?.setNewData(currentUsers)

        for (item in allUsers){
            item.isDisable=false
            item.isCheck=false
        }
        for (item in allUsers){
            for (ite in currentUsers){
                if (ite.studentId==item.studentId){
                    item.isDisable=true
                }
            }
        }
        mAllAdapter?.setNewData(allUsers)
    }

    override fun onSuccess() {
        mPresenter.getClassChildUser(mClassGroup!!.classId)
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_child_manage
    }

    override fun initData() {
        initChangeScreenData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        mPresenter.getClassUser(mClassGroup!!.classGroupId)
    }

    override fun initChangeScreenData() {
        mPresenter = ClassGroupChildPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(mClassGroup?.name+"    管理")
        tv_class_name.text=intent.getStringExtra("className")+"  学生名单"
        tv_class_child_name.text=mClassGroup?.name+"  学生名单"

        tv_add.setOnClickListener {
            val selectIds= mutableListOf<Int>()
            for (item in allUsers){
                if (item.isCheck)
                    selectIds.add(item.studentId)
            }
            if (selectIds.size>0){
                mPresenter.addGroupUsers(mClassGroup?.classId!!, mClassGroup?.classGroupId!!, selectIds)
            }
        }

        tv_out.setOnClickListener {
            val selectIds= mutableListOf<Int>()
            for (item in currentUsers){
                if (item.isCheck)
                    selectIds.add(item.studentId)
            }
            if (selectIds.size>0){
                mPresenter.outClassUsers(mClassGroup?.classId!!, mClassGroup?.classGroupId!!, selectIds)
            }
        }

        initRecycleView()
        initRecycleViewAll()
    }

    private fun initRecycleView(){
        rv_list.layoutManager = GridLayoutManager(this,6)
        mCurrentAdapter = ClassGroupUserSelectorAdapter(R.layout.item_classgroup_user_selector, null)
        rv_list.adapter = mCurrentAdapter
        mCurrentAdapter?.bindToRecyclerView(rv_list)
        mCurrentAdapter?.setOnItemClickListener { adapter, view, position ->
            val item= currentUsers[position]
            item.isCheck=!item.isCheck
            mCurrentAdapter?.notifyItemChanged(position)
        }
    }

    private fun initRecycleViewAll(){
        rv_list_all.layoutManager = GridLayoutManager(this,6)
        mAllAdapter = ClassGroupUserSelectorAdapter(R.layout.item_classgroup_user_selector, null)
        rv_list_all.adapter = mAllAdapter
        mAllAdapter?.bindToRecyclerView(rv_list_all)
        mAllAdapter?.setOnItemClickListener  { adapter, view, position ->
            val item= allUsers[position]
            if (!item.isDisable){
                item.isCheck=!item.isCheck
                mAllAdapter?.notifyItemChanged(position)
            }
        }
    }

    override fun onRefreshData() {
        mPresenter.getClassUser(mClassGroup!!.classGroupId)
    }

}