package com.bll.lnkteacher.ui.activity

import PopupClick
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.presenter.ClassGroupUserPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ClassGroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class ClassGroupUserActivity : BaseActivity(), IContractView.IClassGroupUserView {

    private val mPresenter = ClassGroupUserPresenter(this)
    private var mClassGroup: ClassGroup? = null
    private var users = mutableListOf<ClassGroupUser>()
    private var mAdapter: ClassGroupUserAdapter? = null
    private var position = 0
    private var job = ""
    private var isCreate = false
    private var popupBeans = mutableListOf<PopupBean>()
    private var classItems= mutableListOf<ItemList>()
    private var classPos=0

    override fun onUserList(users: MutableList<ClassGroupUser>) {
        this.users = users
        mAdapter?.setNewData(users)
    }

    override fun onOutSuccess() {
        showToast(R.string.classgroup_kick_success)
        users.removeAt(position)
        mAdapter?.notifyDataSetChanged()
        refreshData()
    }

    override fun onCreateSuccess() {
        showToast("创建班级子群成功")
        refreshData()
    }

    override fun onAddSuccess() {
        showToast("加入${classItems[classPos].name}成功")
        refreshData()
    }

    override fun onEditSuccess() {
        showToast(R.string.classgroup_set_job_success)
        users[position].job = job
        mAdapter?.notifyDataSetChanged()
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        isCreate = mClassGroup?.userId == mUserId
        mPresenter.getClassList(mClassGroup!!.classId)

        popupBeans.add(PopupBean(0, "创建子群"))
        popupBeans.add(PopupBean(1, "添加子群"))
        popupBeans.add(PopupBean(2, "教师详情"))

        for (item in DataBeanManager.classGroups){
            if (item.classGroupId==mClassGroup?.classGroupId&&item.state!=1){
                classItems.add(ItemList(item.classId,item.name))
            }
        }
    }

    override fun initView() {
        setPageTitle(R.string.details)
        if (mClassGroup?.state == 1) {
            showView(iv_manager)
        }

        iv_manager?.setOnClickListener {
            PopupClick(this, popupBeans, iv_manager, 10).builder()
                .setOnSelectListener { item ->
                    when (item.id) {
                        0 -> {
                            val titleStr = mClassGroup?.name + "（${mUser?.subjectName}）"
                            ClassGroupCreateDialog(this, titleStr, mClassGroup?.grade!!,1).builder().setOnDialogClickListener { name, grade ->
                                if (getCheckStudent().isEmpty()){
                                    showToast("未选择学生")
                                    return@setOnDialogClickListener
                                }
                                mPresenter.createGroup(mClassGroup?.classGroupId!!,name, getCheckStudent())
                            }
                        }
                        1 -> {
                            ItemSelectorDialog(this,"子群选择",classItems).builder().setOnDialogClickListener{
                                classPos=it
                                if (getCheckStudent().isEmpty()){
                                    showToast("未选择学生")
                                    return@setOnDialogClickListener
                                }
                                mPresenter.addGroup(classItems[it].id,mClassGroup?.classGroupId!!,getCheckStudent())
                            }
                        }
                        2->{
                            val intent = Intent(this, ClassGroupTeacherActivity::class.java)
                            val bundle = Bundle()
                            bundle.putSerializable("classGroup", mClassGroup)
                            intent.putExtra("bundle", bundle)
                            startActivity(intent)
                        }
                    }
                }
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, mClassGroup?.state!!, isCreate, users)
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
                R.id.iv_check -> {
                    users[position].isCheck = !users[position].isCheck
                    mAdapter?.notifyItemChanged(position)
                }
            }
        }

    }

    /**
     * 选中学生
     */
    private fun getCheckStudent():List<Int>{
        val ids = mutableListOf<Int>()
        for (ite in users) {
            if (ite.isCheck) {
                ids.add(ite.studentId)
            }
        }
        return ids
    }

    private fun refreshData(){
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
        for (item in users){
            item.isCheck=false
        }
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * 踢出学生
     */
    private fun outDialog() {
        CommonDialog(this).setContent(R.string.classgroup_is_kick_tips).builder().setDialogClickListener(object :
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

}