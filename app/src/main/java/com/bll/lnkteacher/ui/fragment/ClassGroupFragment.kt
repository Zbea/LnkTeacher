package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ClassGroupAddDialog
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.ClassGroupChildActivity
import com.bll.lnkteacher.ui.activity.ClassGroupUserActivity
import com.bll.lnkteacher.ui.activity.MainCourseActivity
import com.bll.lnkteacher.ui.adapter.ClassGroupAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class ClassGroupFragment:BaseFragment(),IContractView.IClassGroupView {
    private var mGroupPresenter=ClassGroupPresenter(this)
    private var classGroups= mutableListOf<ClassGroup>()
    private var mAdapter:ClassGroupAdapter?=null
    private var position=0

    override fun onSuccess() {
        fetchCommonData()
    }
    override fun onUploadSuccess() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_classgroup
    }

    override fun initView() {
        setTitle(R.string.main_classgroup_title)
        showView(iv_create,iv_add)

        initRecyclerView()
        onClassGroupEvent()

        iv_create.setOnClickListener {
            createClassGroup()
        }

        iv_add.setOnClickListener {
            addClassGroup()
        }

    }
    override fun lazyLoad() {
        fetchCommonData()
    }

    private fun initRecyclerView(){
        mAdapter=ClassGroupAdapter(R.layout.item_classgroup,classGroups)
        rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0,0,0,DP2PX.dip2px(activity,30f)))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            this.position=position
            val classGroup=classGroups[position]
            //是否是班主任
            val isHeader=classGroup.userId==mUserId
            when(view.id){
                R.id.tv_course->{
                    if (classGroup.state==1){
                        if (isHeader){
                            startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                .putExtra("classGroupId", classGroup.classGroupId)
                            )
                        }
                        else{
                            ImageDialog(requireActivity(), arrayListOf(classGroup.imageUrl)).builder()
                        }
                    }
                }
                R.id.tv_edit->{
                    if (classGroup.state==1&&isHeader){
                        ClassGroupCreateDialog(requireContext(),classGroup.name,classGroup.grade).builder()
                            .setOnDialogClickListener {name,grade->
                                mGroupPresenter.editClassGroup(name,grade,classGroup.classGroupId)
                            }
                    }
                }
                R.id.tv_child->{
                    val intent=Intent(activity, ClassGroupChildActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("classGroup",classGroup)
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                R.id.tv_dissolve->{
                    dissolveGroup()
                }
                R.id.tv_detail->{
                    val intent=Intent(activity, ClassGroupUserActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("classGroup",classGroup)
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
            }

        }

    }

    /**
     * 创建班群
     */
    private fun createClassGroup(){
        ClassGroupCreateDialog(requireContext()).builder()
            .setOnDialogClickListener {name,grade->
                mGroupPresenter.createClassGroup(name,grade)
            }
    }

    /**
     * 加入班群
     */
    private fun addClassGroup(){
        ClassGroupAddDialog(requireActivity()).builder().setOnDialogClickListener{
            mGroupPresenter.addClassGroup(it)
        }
    }


    /**
     * 解散班群
     */
    private fun dissolveGroup(){
        val classId=classGroups[position].classId
        val boolean=classGroups[position].userId==mUserId
        val titleStr=if (boolean) "确定解散班群？" else "确定退出班群？"
        CommonDialog(requireActivity()).setContent(titleStr).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    if (boolean){
                        mGroupPresenter.dissolveClassGroup(classId)
                    }
                    else{
                        mGroupPresenter.outClassGroup(classId)
                    }
                }
            })
    }


    override fun onClassGroupEvent() {
        classGroups.clear()
        for (item in DataBeanManager.classGroups){
            if (item.state==1){
                classGroups.add(item)
            }
        }
        mAdapter?.setNewData(classGroups)
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.CLASSGROUP_EVENT){
            lazyLoad()
        }
    }

}