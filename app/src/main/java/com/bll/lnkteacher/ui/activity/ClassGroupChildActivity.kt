package com.bll.lnkteacher.ui.activity

import PopupClick
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ClassGroupChildCreateDialog
import com.bll.lnkteacher.dialog.ClassGroupUserSelectorDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupChildPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupChildAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*

class ClassGroupChildActivity : BaseActivity(), IContractView.IClassGroupChildView {

    private lateinit var mPresenter :ClassGroupChildPresenter
    private var mClassGroup: ClassGroup? = null
    private var users = mutableListOf<ClassGroupUser>()
    private var classGroupChilds = mutableListOf<ClassGroup>()
    private var mAdapter: ClassGroupChildAdapter? = null

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        this.users = users
    }

    override fun onClassGroupChildList(classItems: MutableList<ClassGroup>) {
        classGroupChilds = classItems
        mAdapter?.setNewData(classGroupChilds)
        rv_list.callOnClick()
//        Handler(mainLooper).post { mAdapter!!.notifyDataSetChanged() }
    }

    override fun onSuccess() {
        mPresenter.getClassGroupChild(mClassGroup?.classGroupId!!)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        mPresenter.getClassUser(mClassGroup!!.classId)
        mPresenter.getClassGroupChild(mClassGroup?.classGroupId!!)
    }

    override fun initChangeScreenData() {
        mPresenter = ClassGroupChildPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("子群")
        showView(iv_manager)
        disMissView(ll_page_number)
        iv_manager.setImageResource(R.mipmap.icon_add)

        iv_manager.setOnClickListener {
            val titleStr=mClassGroup?.name!!+"(${mUser?.subjectName})"
            for (user in users){
                user.isCheck=false
            }
            ClassGroupChildCreateDialog(this,titleStr ,users).builder().setOnDialogClickListener { name, ids ->
                mPresenter.createGroup(mClassGroup?.classGroupId!!, name, ids)
            }
        }

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f),DP2PX.dip2px(this,30f),DP2PX.dip2px(this,50f),40)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupChildAdapter(R.layout.item_classgroup_child, null)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0,60))
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=classGroupChilds[position]
            when (view.id) {
                R.id.tv_add -> {
                    ClassGroupUserSelectorDialog(this, users).builder().setOnDialogClickListener {
                        mPresenter.addGroup(item.classId, mClassGroup?.classGroupId!!, it)
                    }
                }
                R.id.tv_edit->{
                    InputContentDialog(this,item.name).builder().setOnDialogClickListener{
                        mPresenter.editClassGroup(it,item.classId)
                    }
                }
                R.id.tv_dissolve->{
                    CommonDialog(this).setContent("确定解散该子群？").builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                mPresenter.dissolveClassGroup(item.classId)
                            }
                        })
                }
            }
        }
        mAdapter?.setCustomItemChildClickListener { position, view, studentId ->
            val classItems = mutableListOf<PopupBean>()
            for (i in classGroupChilds.indices) {
                if (i != position) {
                    val item = classGroupChilds[i]
                    classItems.add(PopupBean(item.classId, item.name))
                }
            }
            classItems.add(PopupBean(0, "踢出"))
            PopupClick(this, classItems, view, 5).builder().setOnSelectListener {
                if (it.id == 0) {
                    mPresenter.outClassUser(classGroupChilds[position].classId, mClassGroup?.classGroupId!!, studentId)
                } else {
                    val map = HashMap<String, Any>()
                    map["classId"] = classGroupChilds[position].classId
                    map["classGroupId"] = mClassGroup?.classGroupId!!
                    map["newClassId"] = it.id
                    map["studentIds"] = arrayOf(studentId)
                    mPresenter.moveClassUser(map)
                }
            }
        }
    }

}