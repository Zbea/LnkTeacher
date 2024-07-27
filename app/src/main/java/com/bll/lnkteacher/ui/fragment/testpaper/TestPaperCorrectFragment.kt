package com.bll.lnkteacher.ui.fragment.testpaper

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.AnalyseActivity
import com.bll.lnkteacher.ui.activity.teaching.CorrectActivity
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class TestPaperCorrectFragment: BaseFragment(),IContractView.ITestPaperCorrectView {

    private val mPresenter=TestPaperCorrectPresenter(this)
    private var mAdapter: TestPaperCorrectAdapter?=null
    private var items= mutableListOf<CorrectBean>()
    private var pos=0

    override fun onList(bean: CorrectList) {
        setPageNumber(bean.total)
        items= bean.list
        mAdapter?.setNewData(items)
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        mAdapter?.remove(pos)
    }

    override fun onSendSuccess() {
        showToast("批改发送成功")
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
        val layoutParams=LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(DP2PX.dip2px(activity,40f),DP2PX.dip2px(activity,30f),DP2PX.dip2px(activity,40f),0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = TestPaperCorrectAdapter(R.layout.item_testpaper_correct, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { _, view, position ->
                pos=position
                val item=items[position]
                when(view.id){
                    R.id.tv_analyse->{
                        val intent=Intent(requireActivity(), AnalyseActivity::class.java)
                        val bundle=Bundle()
                        bundle.putSerializable("paperCorrect",item)
                        intent.putExtra("bundle",bundle)
                        intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
                        customStartActivity(intent)
                    }
                    R.id.iv_delete->{
                        deleteCorrect()
                    }
                }
            }
            setOnChildClickListener { view,parentPos, position ->
                val item=items[parentPos]
                if (view.id==R.id.ll_content){
                    val intent= Intent(requireActivity(), CorrectActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("paperCorrect",item)
                    intent.putExtra("bundle",bundle)
                    intent.putExtra("classPos",position)
                    intent.putExtra(Constants.INTENT_SCREEN_LABEL,Constants.SCREEN_FULL)
                    customStartActivity(intent)
                }
                if (view.id==R.id.tv_send){
                    mPresenter.sendClass(item.examList[position]?.examChangeId!!)
                }
            }
        }
        rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(requireActivity(),20f)))
    }

    /**
     * 删除批改
     */
    private fun deleteCorrect(){
        CommonDialog(requireActivity()).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                mPresenter.deleteCorrect(items[pos].id)
            }
        })
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
        if (grade==0)
            return
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["taskType"]=2
        map["grade"]=grade
        mPresenter.getList(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if(msgFlag== Constants.EXAM_CORRECT_EVENT){
            fetchData()
        }
    }

}