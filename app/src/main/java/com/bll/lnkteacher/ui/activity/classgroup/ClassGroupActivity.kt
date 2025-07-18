package com.bll.lnkteacher.ui.activity.classgroup

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.ClassGroupAddDialog
import com.bll.lnkteacher.dialog.ClassGroupChildCreateDialog
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.ClassGroupPermissionDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn_1
import kotlinx.android.synthetic.main.common_title.tv_custom_1
import kotlinx.android.synthetic.main.common_title.tv_custom_2
import org.greenrobot.eventbus.EventBus

class ClassGroupActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private var mGroupPresenter = ClassGroupPresenter(this)
    private var classGroups = mutableListOf<ClassGroup>()
    private var mAdapter: ClassGroupAdapter? = null
    private var position = 0
    private var classGroupAddDialog: ClassGroupAddDialog? = null
    private var permissionTime=0L

    override fun onClasss(groups: MutableList<ClassGroup>) {
        DataBeanManager.classGroups = groups
        classGroups = groups
        mAdapter?.setNewData(classGroups)
    }

    override fun onClassInfo(classGroup: ClassGroup) {
        if (classGroup.name == null) {
            classGroupAddDialog?.setTextInfo("")
        } else {
            val info = "班级信息：${classGroup.name} ${classGroup.teacher}"
            classGroupAddDialog?.setTextInfo(info)
        }
    }

    override fun onSuccess() {
        mGroupPresenter.getClassGroups()
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

    override fun onAllowSuccess() {
        val classGroup=classGroups[position]
        if (classGroup.isAllowJoin==2){
            classGroup.isAllowJoin=1
        }
        else{
            classGroup.isAllowJoin=2
        }
        mAdapter?.notifyItemChanged(position)
    }

    override fun onPermission() {
        val classGroup=classGroups[position]
        classGroup.permissionTime=permissionTime
        mAdapter?.notifyItemChanged(position)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        if (NetworkUtil.isNetworkConnected()) {
            mGroupPresenter.getClassGroups()
        } else {
            classGroups = DataBeanManager.classGroups
        }
    }

    override fun initView() {
        setPageTitle(R.string.main_classgroup_title)
        showView(tv_custom_1, tv_custom_2, tv_btn_1)
        tv_custom_1.text = "创建班群"
        tv_custom_2.text = "创建辅群"
        tv_btn_1.text = "加群"

        tv_custom_1.setOnClickListener {
            createClassGroup(1)
        }
        tv_custom_2.setOnClickListener {
            createClassGroup(2)
        }

        tv_btn_1.setOnClickListener {
            addClassGroup()
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this, 40f), DP2PX.dip2px(this, 30f), DP2PX.dip2px(this, 40f), 40)
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        mAdapter = ClassGroupAdapter(R.layout.item_classgroup, classGroups)
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this, 20f)))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val classGroup = classGroups[position]
            val intent = Intent(this, if (classGroup.state == 1) ClassGroupUserActivity::class.java else ClassGroupChildUserActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", classGroup)
            intent.putExtra("bundle", bundle)
            customStartActivity(intent)
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            val classGroup = classGroups[position]
            when (view.id) {
                R.id.tv_edit -> {
                    if (classGroup.state == 1) {
                        ClassGroupCreateDialog(this, classGroup.type, classGroup.name, classGroup.grade).builder()
                            .setOnDialogClickListener { name, grade ->
                                mGroupPresenter.editClassGroup(name, grade, classGroup.classId, classGroup.classGroupId)
                            }
                    } else {
                        ClassGroupChildCreateDialog(this, classGroup.name, 1).builder().setOnDialogClickListener { name ->
                            mGroupPresenter.editClassGroupChild(name, classGroup.classId)
                        }
                    }
                }

                R.id.tv_child -> {
                    if (classGroup.state == 1) {
                        val titleStr = classGroups[position].name!! + "(${mUser?.subjectName})"
                        ClassGroupChildCreateDialog(this, titleStr).builder().setOnDialogClickListener { name ->
                            mGroupPresenter.createGroupChild(classGroups[position].classGroupId, name)
                        }
                    } else {
                        val className = getClassName(classGroup.classGroupId)
                        val intent = Intent(this, ClassGroupChildManageActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("classGroup", classGroup)
                        intent.putExtra("bundle", bundle)
                        intent.putExtra("className", className)
                        customStartActivity(intent)
                    }
                }

                R.id.tv_dissolve -> {
                    dissolveGroup()
                }

                R.id.tv_permission -> {
                    if (classGroup.permissionTime>System.currentTimeMillis()){
                        CommonDialog(this).setContent("确定关闭权限？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                permissionTime=0
                                mGroupPresenter.setClassGroupPermission(classGroup.classGroupId,permissionTime)
                            }
                        })
                    }
                    else{
                        ClassGroupPermissionDialog(this).builder().setOnDialogClickListener{
                            val time=it*60*1000
                            permissionTime=System.currentTimeMillis()+time
                            mGroupPresenter.setClassGroupPermission(classGroup.classGroupId,permissionTime)
                        }
                    }
                }

                R.id.iv_arrow_join->{
                    val titleInfo=if (classGroup.isAllowJoin==2) "开启班群，允许学生加入班群？" else "关闭班群，不允许学生加入班群？"
                    CommonDialog(this).setContent(titleInfo).builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            mGroupPresenter.allowJoinGroup(classGroup.classId,if (classGroup.isAllowJoin==2) 1 else 2)
                        }
                    }
                }
            }

        }

    }

    /**
     * 获取主群名称
     */
    private fun getClassName(classGroupId: Int): String {
        var classStr = ""
        for (item in classGroups) {
            if (item.classGroupId == classGroupId && item.state == 1) {
                classStr = item.name
            }
        }
        return classStr
    }

    /**
     * 创建班群
     */
    private fun createClassGroup(type: Int) {
        ClassGroupCreateDialog(this, type).builder()
            .setOnDialogClickListener { name, grade ->
                mGroupPresenter.createClassGroup(name, grade, type)
            }
    }

    /**
     * 加入班群
     */
    private fun addClassGroup() {
        classGroupAddDialog = ClassGroupAddDialog(this).builder()
        classGroupAddDialog?.setOnDialogClickListener(object : ClassGroupAddDialog.OnDialogClickListener {
            override fun onClick(code: Int) {
                mGroupPresenter.addClassGroup(code)
            }

            override fun onEditTextCode(code: Int) {
                mGroupPresenter.onClassGroupInfo(code)
            }
        })
    }


    /**
     * 解散班群
     */
    private fun dissolveGroup() {
        val classGroup = classGroups[position]
        val classId = classGroup.classId
        val boolean = classGroup.userId == mUserId
        val titleStr = if (boolean) "确定解散${classGroup.name}？" else "确定退出${classGroup.name}？"
        CommonDialog(this).setContent(titleStr).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {
                    if (boolean) {
                        mGroupPresenter.dissolveClassGroup(classId)
                    } else {
                        mGroupPresenter.outClassGroup(classId)
                    }
                }
            })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.CLASSGROUP_INFO_EVENT) {
            mGroupPresenter.getClassGroups()
        }
    }

    override fun onRefreshData() {
        mGroupPresenter.getClassGroups()
    }

}