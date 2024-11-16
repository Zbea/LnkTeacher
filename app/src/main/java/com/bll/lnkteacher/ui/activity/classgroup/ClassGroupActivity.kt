package com.bll.lnkteacher.ui.activity.classgroup

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ClassGroupAddDialog
import com.bll.lnkteacher.dialog.ClassGroupChildCreateDialog
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.ClassScheduleActivity
import com.bll.lnkteacher.ui.adapter.ClassGroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn_1
import kotlinx.android.synthetic.main.common_title.tv_btn_2

class ClassGroupActivity : BaseActivity(), IContractView.IClassGroupView {

    private lateinit var mGroupPresenter: ClassGroupPresenter
    private var classGroups = mutableListOf<ClassGroup>()
    private var mAdapter: ClassGroupAdapter? = null
    private var position = 0

    override fun onClasss(groups: MutableList<ClassGroup>) {
        DataBeanManager.classGroups = groups
        classGroups = groups
        mAdapter?.setNewData(classGroups)
    }

    override fun onSubjects(subjects: MutableList<String>?) {
    }

    override fun onSuccess() {
        mGroupPresenter.getClassGroups()
    }

    override fun onUploadSuccess() {
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        mGroupPresenter.getClassGroups()
    }

    override fun initChangeScreenData() {
        mGroupPresenter = ClassGroupPresenter(this, getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(R.string.main_classgroup_title)
        showView(tv_btn_1, tv_btn_2)
        tv_btn_2.text = "建群"
        tv_btn_1.text = "加群"

        tv_btn_2.setOnClickListener {
            createClassGroup()
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
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position = position
            val classGroup = classGroups[position]
            //是否是班主任
            val isHeader = classGroup.userId == mUserId
            when (view.id) {
                R.id.tv_course -> {
                    if (classGroup.state == 1) {
                        if (isHeader) {
                            customStartActivity(Intent(this, ClassScheduleActivity::class.java)
                                    .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                    .putExtra("classGroupId", classGroup.classGroupId))
                        } else {
                            ImageDialog(this, arrayListOf(classGroup.imageUrl)).builder()
                        }
                    }
                }

                R.id.tv_edit -> {
                    if (classGroup.state == 1) {
                        ClassGroupCreateDialog(this, classGroup.name, classGroup.grade).builder()
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

                R.id.tv_detail -> {
                    val intent = Intent(this,if (classGroup.state==1) ClassGroupUserActivity::class.java else ClassGroupChildUserActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("classGroup", classGroup)
                    intent.putExtra("bundle", bundle)
                    customStartActivity(intent)
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
    private fun createClassGroup() {
        ClassGroupCreateDialog(this).builder()
            .setOnDialogClickListener { name, grade ->
                mGroupPresenter.createClassGroup(name, grade)
            }
    }

    /**
     * 加入班群
     */
    private fun addClassGroup() {
        ClassGroupAddDialog(this).builder().setOnDialogClickListener {
            mGroupPresenter.addClassGroup(it)
        }
    }


    /**
     * 解散班群
     */
    private fun dissolveGroup() {
        val classGroup=classGroups[position]
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
        if (msgFlag == Constants.CLASSGROUP_EVENT) {
            mGroupPresenter.getClassGroups()
        }
    }

    override fun onRefreshData() {
        mGroupPresenter.getClassGroups()
    }

}