package com.bll.lnkteacher.ui.fragment

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.HandoutList
import com.bll.lnkteacher.mvp.model.HandoutList.HandoutBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.HandoutPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.HandoutsAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_fragment_title.tv_type
import kotlinx.android.synthetic.main.fragment_list.rv_list
import java.io.File

class LearningConditionFragment: BaseFragment(), IContractView.IHandoutView {
    private val presenter= HandoutPresenter(this,1)
    private var type=""
    private val handoutsTypes= mutableListOf<PopupBean>()
    private var types= mutableListOf<String>()
    private var mAdapter: HandoutsAdapter? = null
    private var items = mutableListOf<HandoutBean>()
    private var position = 0

    override fun onType(list: MutableList<String>) {
        if (list.size>0){
            if (types!=list){
                handoutsTypes.clear()
                for (i in list.indices){
                    handoutsTypes.add(PopupBean(i,list[i],i==0))
                }
                types=list
                tv_type.text=list[0]
                type=list[0]
                fetchData()
            }
        }
        else{
            disMissView(tv_type)
        }
    }

    override fun onList(list: HandoutList) {
        setPageNumber(list.total)
        for (item in list.list){
            val fileName=item.id.toString()
            item.bookPath= FileAddress().getPathBook(fileName+MethodManager.getUrlFormat(item.bodyUrl))
            item.bookDrawPath= FileAddress().getPathBookDraw(fileName)
        }
        items=list.list
        mAdapter?.setNewData(items)
    }

    override fun onSuccess() {
        showToast(1,"删除成功")
        val item =items[position]
        if (FileUtils.isExist(item.bookPath)){
            FileUtils.deleteFile(File(item.bookPath))
            FileUtils.deleteFile(File(item.bookDrawPath))
        }
        fetchData()
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_learning_condition
    }

    override fun initView() {
        pageSize=30
        setTitle(DataBeanManager.getIndexLeftData()[3].name)
        showView(tv_type)

        tv_type.setOnClickListener {
            PopupRadioList(requireActivity(),handoutsTypes,tv_type,5).builder().setOnSelectListener{
                if (type!=it.name){
                    type=it.name
                    pageIndex=1
                    fetchData()
                }
            }
        }

        initRecyclerView()
    }

    override fun lazyLoad() {
        presenter.getTypeList()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),50f), DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
        mAdapter = HandoutsAdapter(R.layout.item_handout, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(5, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position = position
            val item=items[position]
            if (FileUtils.isExist(item.bookPath)){
                MethodManager.gotoHandouts(requireActivity(),item)
            }
            else{
                downLoadStart()
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                delete()
                true
            }
    }

    /**
     * 下载解压书籍
     */
    private fun downLoadStart() {
        showLoading()
        val item=items[position]
        FileDownManager.with(requireActivity()).create(item.bodyUrl).setPath(item.bookPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    mAdapter?.notifyItemChanged(position)
                    showToast(1,item.title+getString(R.string.book_download_success))
                    hideLoading()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast(1,"${item.title}下载失败")
                }
            })
    }

    private fun delete(){
        CommonDialog(requireActivity(),1).setContent("确定删除文件？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val map=HashMap<String,Any>()
                map["id"]=items[position].id
                presenter.delete(map)
            }
        })
    }


    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=type
        presenter.getList(map)
    }

    override fun onRefreshData() {
        lazyLoad()
    }
}