package com.bll.lnkteacher.ui.fragment.textbook

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.HandoutDaoManager
import com.bll.lnkteacher.mvp.model.HandoutBean
import com.bll.lnkteacher.mvp.model.HandoutList
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.presenter.HandoutPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.HandoutsAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list.rv_list
import java.io.File

class HandoutFragment : BaseMainFragment(), IContractView.IHandoutView {
    private val presenter = HandoutPresenter(this, 1)
    private var mAdapter: HandoutsAdapter? = null
    private var items = mutableListOf<HandoutBean>()
    private var position = 0
    private var type = ""
    private var editTitleStr = ""
    private var isNet = true

    override fun onList(list: HandoutList) {
        setPageNumber(list.total)
        for (item in list.list) {
            val fileName = MD5Utils.digest(item.id.toString())
            item.bookPath = FileAddress().getPathHandout(fileName + FileUtils.getUrlFormat(item.bodyUrl))
            item.bookDrawPath = FileAddress().getPathHandoutDraw(fileName)
        }
        DataBeanManager.handouts=list.list
        items = list.list
        mAdapter?.setNewData(items)
    }

    override fun onDelete() {
        showToast(1, "删除成功")
        val item = items[position]
        if (FileUtils.isExist(item.bookPath)) {
            FileUtils.deleteFile(File(item.bookPath))
            FileUtils.deleteFile(File(item.bookDrawPath))
            HandoutDaoManager.getInstance().deleteBean(item)
        }
        fetchData()
    }

    override fun onEdit() {
        items[position].title = editTitleStr
        mAdapter?.notifyItemChanged(position)
        if (HandoutDaoManager.getInstance().isExist(items[position].id)){
            val item=HandoutDaoManager.getInstance().queryBean(items[position].id)
            item.title=editTitleStr
            HandoutDaoManager.getInstance().insertOrReplace(item)
        }
    }

    override fun onTop() {
        pageIndex = 1
        fetchData()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize = 25
        initDialog(1)
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(), 20f), DP2PX.dip2px(requireActivity(), 30f), DP2PX.dip2px(requireActivity(), 20f), 0)
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 5)//创建布局管理
        mAdapter = HandoutsAdapter(R.layout.item_handout, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(5, 20))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position = position
            val item = items[position]
            if (HandoutDaoManager.getInstance().isExist(item.id)) {
                when(FileUtils.getUrlFormat(item.bookPath)){
                    ".ppt",".pptx"->{
                        MethodManager.gotoPptDetails(requireActivity(),item.bookPath,Constants.SCREEN_LEFT)
                    }
                    ".pdf"->{
                        MethodManager.gotoHandouts(requireActivity(), item)
                    }
                }
            } else {
                downLoadStart()
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                onLongClick()
                true
            }
    }

    private fun onLongClick() {
        val item=items[position]

        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = "删除"
            resId = R.mipmap.icon_setting_delete
        })
        beans.add(ItemList().apply {
            name = "重命名"
            resId = R.mipmap.icon_setting_edit
        })

        LongClickManageDialog(requireActivity(),1, item.title, beans).builder()
            .setOnDialogClickListener { position->
                val map = HashMap<String, Any>()
                map["id"] = item.id
                when(position){
                    0->{
                        presenter.delete(map)
                    }
                    1->{
                        InputContentDialog(requireActivity(),1,item.title).builder().setOnDialogClickListener{
                            editTitleStr=it
                            map["title"]=it
                            presenter.edit(map)
                        }
                    }
                    2->{
                        presenter.top(map)
                    }
                }
            }
    }

    /**
     * 下载解压书籍
     */
    private fun downLoadStart() {
        showLoading()
        val item = items[position]
        FileDownManager.with(requireActivity()).create(item.bodyUrl).setPath(item.bookPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    item.date = System.currentTimeMillis()
                    HandoutDaoManager.getInstance().insertOrReplace(item)
                    mAdapter?.notifyItemChanged(position)
                    hideLoading()
                    when(FileUtils.getUrlFormat(item.bookPath)){
                        ".ppt",".pptx"->{
                            MethodManager.gotoPptDetails(requireActivity(),item.bookPath,Constants.SCREEN_LEFT)
                        }
                        ".pdf"->{
                            MethodManager.gotoHandouts(requireActivity(), item)
                        }
                    }
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(1, "${item.title}下载失败")
                }
            })
    }


    fun changeType(type: String) {
        this.type = type
        pageIndex = 1
        fetchData()
    }

    override fun fetchData() {
        if (NetworkUtil.isNetworkConnected()) {
            if (!isNet)
                pageIndex = 1
            isNet = true
            val map = HashMap<String, Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"] = type
            presenter.getList(map)
        } else {
            if (isNet)
                pageIndex = 1
            isNet = false
            val total = HandoutDaoManager.getInstance().queryAll(type).size
            setPageNumber(total)
            items = HandoutDaoManager.getInstance().queryAll(type, pageIndex, pageSize)
            mAdapter?.setNewData(items)
        }
    }

    override fun onRefreshData() {
        fetchData()
    }

}