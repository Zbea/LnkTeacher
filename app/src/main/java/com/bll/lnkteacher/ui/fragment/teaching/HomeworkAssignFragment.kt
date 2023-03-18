package com.bll.lnkteacher.ui.fragment.teaching

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.HomeworkPublishDialog
import com.bll.lnkteacher.mvp.model.homework.HomeworkType
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.HomeworkAssignContentActivity
import com.bll.lnkteacher.ui.adapter.HomeworkAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class HomeworkAssignFragment:BaseFragment(),IContractView.IHomeworkAssignView {

    private val mPresenter=HomeworkAssignPresenter(this)
    private var mAdapter:HomeworkAssignAdapter?=null
    private var types= mutableListOf<HomeworkType.TypeBean>()
    private var position=0
    private var grade=1//年级

    override fun onTypeList(homeworkType: HomeworkType) {
        setPageNumber(homeworkType.total)
        types=homeworkType.list
        mAdapter?.setNewData(types)
    }
    override fun onAddSuccess() {
        if (types.size==pageSize){
            pageIndex+=1
        }
        fetchData()
    }
    override fun onList(homeworkContent: AssignContent?) {
    }
    override fun onImageList(lists: MutableList<ContentListBean>?) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=9
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,60f),
            DP2PX.dip2px(activity,40f), 0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)

        mAdapter = HomeworkAssignAdapter(R.layout.item_homework_assign, types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3,DP2PX.dip2px(requireActivity(),50f)))
            setOnItemClickListener { adapter, view, position ->
                this@HomeworkAssignFragment.position=position
                if(types[position].subType==1){
                    val intent=Intent(activity, HomeworkAssignContentActivity::class.java)
                    val bundle=Bundle()
                    bundle.putSerializable("homeworkType",types[position])
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)

                }
                else{
                    HomeworkPublishDialog(requireContext()).builder().setOnDialogClickListener{
                            contentStr,homeClasss->

                    }
                }
            }
        }

    }

    /**
     * 添加作业本
     */
    fun changeData(item: HomeworkType.TypeBean,subType:Int){
        val map=HashMap<String,Any>()
        map["commonTypeId"]=item.subType
        map["name"]=item.title
        map["type"]=1
        map["subType"]=subType
        map["title"]=item.title
        map["grade"]=grade
        mPresenter.addType(map)
    }

    /**
     * 刷新年级
     */
    fun changeGrade(grade:Int){
        this.grade=grade
        fetchData()
    }

    override fun onRefreshData() {
       fetchData()
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        mPresenter.getTypeList(map)
    }
}