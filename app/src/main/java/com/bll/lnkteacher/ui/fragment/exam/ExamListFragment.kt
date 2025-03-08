package com.bll.lnkteacher.ui.fragment.exam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupCheckList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.presenter.ExamListPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IExamListView
import com.bll.lnkteacher.ui.activity.exam.ExamAnalyseActivity
import com.bll.lnkteacher.ui.activity.exam.ExamDetailsActivity
import com.bll.lnkteacher.ui.adapter.ExamListAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class ExamListFragment:BaseFragment(),IExamListView{
    private var mPresenter=ExamListPresenter(this,2)
    private var mAdapter:ExamListAdapter?=null
    private var items= mutableListOf<ExamList.ExamBean>()
    private var position=0

    override fun onList(list: ExamList) {
        items=list.list
        mAdapter?.setNewData(items)
        setPageNumber(list.total)
    }

    override fun onDeleteSuccess() {
        mAdapter?.remove(position)
    }

    override fun onSendSuccess() {
        showToast("批改考卷发送成功")
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=3
        initRecyclerView()
        initDialog(2)
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = ExamListAdapter(R.layout.item_testpaper_correct, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(requireActivity(),2,items[position].examUrl.split(",")).builder()
            }
            setOnItemChildClickListener { _, view, position ->
                this@ExamListFragment.position=position
                val item=items[position]
                when(view.id){
                    R.id.tv_analyse->{
                        val intent= Intent(requireActivity(), ExamAnalyseActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("examBean",item)
                        intent.putExtra("bundle",bundle)
                        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                        customStartActivity(intent)
                    }
                    R.id.tv_send->{
                        send(view)
                    }
                    R.id.iv_delete->{
                        delete()
                    }
                }
            }
            setOnChildClickListener{view, parentPos, position ->
                val item=items[position]
                if (view.id==R.id.ll_content){
                    if (item.sendStatus==2){
                        val intent= Intent(requireActivity(), ExamDetailsActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("examBean",items[parentPos])
                        intent.putExtra("bundle",bundle)
                        intent.putExtra("classPos",position)
                        intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                        customStartActivity(intent)
                    }
                }
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(requireActivity(),15f)))
    }

    private fun send(view: View){
        val item=items[position]
        CommonDialog(requireActivity()).setContent("确认发送？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val ids= mutableListOf<Int>()
                if (item.classList.size==1){
                    ids.add(item.classList[0].classId)
                    mPresenter.sendClass(item.id,ids)
                }
                else{
                    val pops= mutableListOf<PopupBean>()
                    for (classItem in item.classList){
                        pops.add(PopupBean(classItem.classId,classItem.className))
                    }
                    PopupCheckList(requireActivity(),pops,view,5).builder().setOnSelectListener{
                        for (bean in it){
                            ids.add(bean.id)
                        }
                        mPresenter.sendClass(item.id,ids)
                    }
                }
            }
        })
    }

    private fun delete(){
        CommonDialog(requireActivity()).setContent(R.string.toast_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val map=HashMap<String,Any>()
                    map["id"]=items[position].id
                    mPresenter.deleteExamList(map)
                }
            })
    }

    fun onChangeGrade(grade:Int){
        this.grade=grade
        fetchData()
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        if (grade==0)
            return
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {
            val map = HashMap<String, Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"] = grade
            mPresenter.getExamList(map)
        }
    }

}