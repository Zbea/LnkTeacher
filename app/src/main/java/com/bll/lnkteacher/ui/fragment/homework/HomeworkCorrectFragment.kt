package com.bll.lnkteacher.ui.fragment.homework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.mvp.model.testpaper.CorrectBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.presenter.HomeworkCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IHomeworkCorrectView
import com.bll.lnkteacher.ui.activity.teaching.HomeworkCorrectActivity
import com.bll.lnkteacher.ui.activity.teaching.TestPaperAnalyseActivity
import com.bll.lnkteacher.ui.adapter.HomeworkCorrectAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_teaching_list.*

class HomeworkCorrectFragment:BaseMainFragment(),IHomeworkCorrectView {

    private var mPresenter=HomeworkCorrectPresenter(this)
    private var mAdapter:HomeworkCorrectAdapter?=null
    private var position=0
    private var items= mutableListOf<CorrectBean>()

    override fun onList(list: CorrectList) {
        setPageNumber(list.total)
        items= list.list
        mAdapter?.setNewData(items)
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        mAdapter?.remove(position)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_list
    }

    override fun initView() {
        pageSize=3
        initRecyclerView()
        initDialog(2)
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {

        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight = 1f
        layoutParams.setMargins(DP2PX.dip2px(activity, 40f), DP2PX.dip2px(activity, 30f), DP2PX.dip2px(activity, 40f), 0)
        rv_list.layoutParams = layoutParams
        rv_list.layoutManager = LinearLayoutManager(requireActivity())

        mAdapter = HomeworkCorrectAdapter(R.layout.item_teaching_homework_correct, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(activity, 30f)))
            setOnItemChildClickListener { adapter, view, position ->
                this@HomeworkCorrectFragment.position = position
                val item=items[position]
                when (view.id) {
                    R.id.iv_delete -> {
                        deleteCorrect()
                    }
                    R.id.tv_analyse->{
                        val intent=Intent(requireActivity(), TestPaperAnalyseActivity::class.java)
                        val bundle=Bundle()
                        bundle.putSerializable("paperCorrect",item)
                        intent.putExtra("bundle",bundle)
                        startActivity(intent)
                    }
                }
            }
            setOnChildClickListener { view,parentPos, position ->
                if (view.id==R.id.ll_content){
                    val intent= Intent(requireActivity(), HomeworkCorrectActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("classBean",items[parentPos].examList[position])
                    intent.putExtra("bundle",bundle)
                    intent.putExtra("subType",items[parentPos].subType)
                    intent.flags=items[parentPos].id
                    startActivity(intent)
                }
            }

        }
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
                mPresenter.deleteCorrect(items[position].id)
            }
        })
    }

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
        map["taskType"]=1
        map["grade"]=grade
        mPresenter.getList(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        if(msgFlag==Constants.HOMEWORK_CORRECT_EVENT){
            fetchData()
        }
    }

}