package com.bll.lnkteacher.ui.fragment.exam

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.bll.lnkteacher.mvp.presenter.ExamCorrectListPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IExamCorrectListView
import com.bll.lnkteacher.ui.activity.exam.ExamCorrectActivity
import com.bll.lnkteacher.ui.adapter.ExamCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class ExamCorrectFragment: BaseFragment(),IExamCorrectListView{

    private var mPresenter=ExamCorrectListPresenter(this,2)
    private var mAdapter:ExamCorrectAdapter?=null
    private var corrects= mutableListOf<ExamCorrectList.ExamCorrectBean>()
    private var position=0

    override fun onList(list: ExamCorrectList) {
        corrects=list.list
        mAdapter?.setNewData(corrects)
    }

    override fun onSuccess() {
        mAdapter?.remove(position)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        initRecyclerView()
        initDialog(2)
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected()){
            mPresenter.getExamCorrectList()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity, 30f), DP2PX.dip2px(activity, 50f),
            DP2PX.dip2px(activity, 30f), 0
        )
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter= ExamCorrectAdapter(R.layout.item_exam_correct,null)
            .apply {
                rv_list.layoutManager = GridLayoutManager(activity,2)//创建布局管理
                rv_list.adapter = mAdapter
                bindToRecyclerView(rv_list)
                setEmptyView(R.layout.common_empty)
                rv_list.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(activity, 45f)))
                setOnItemChildClickListener { adapter, view, position ->
                    this@ExamCorrectFragment.position=position
                    if (view.id==R.id.tv_save){
                        CommonDialog(requireActivity(),2).setContent("确认完成班级批改？").builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                mPresenter.onExamCorrectComplete(corrects[position].id)
                            }
                        }
                    }
                }
                setOnItemClickListener { adapter, view, position ->
                    val item=corrects[position]
                    val intent= Intent(requireActivity(), ExamCorrectActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("examBean",item)
                    intent.putExtra("bundle",bundle)
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL, Constants.SCREEN_FULL)
                    customStartActivity(intent)
                }
            }

    }

    override fun onRefreshData() {
        lazyLoad()
    }

}