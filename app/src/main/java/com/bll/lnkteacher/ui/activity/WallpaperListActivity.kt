package com.bll.lnkteacher.ui.activity

import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.WallpaperDaoManager
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.WallpaperBean
import com.bll.lnkteacher.mvp.model.WallpaperList
import com.bll.lnkteacher.mvp.presenter.WallpaperPresenter
import com.bll.lnkteacher.mvp.view.IContractView.IWallpaperView
import com.bll.lnkteacher.ui.adapter.WallpaperAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileMultitaskDownManager
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*

class WallpaperListActivity:BaseActivity(),IWallpaperView {

    private val presenter=WallpaperPresenter(this)
    private var items= mutableListOf<WallpaperBean>()
    private var mAdapter:WallpaperAdapter?=null
    private var position=0
    private var supply=1
    private var pops= mutableListOf<PopupBean>()

    override fun onList(list: WallpaperList) {
        setPageNumber(list.total)
        items=list.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
        items[position].buyStatus=1
        mAdapter?.notifyItemChanged(position)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12
        pops=DataBeanManager.popupSupplys
    }
    override fun initView() {
        setPageTitle("壁纸")

        tv_type.text="官方"
        showView(tv_type)
        setPageSetting("我的壁纸")

        tv_type.setOnClickListener {
            PopupRadioList(this,pops,tv_type,tv_type.width,5).builder().setOnSelectListener{
                supply=it.id
                tv_type.text=it.name
                pageIndex=1
                fetchData()
            }
        }

        tv_setting.setOnClickListener {
            startActivity(Intent(this, WallpaperMyActivity::class.java))
        }

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,28f), DP2PX.dip2px(this,60f),
            DP2PX.dip2px(this,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = WallpaperAdapter(R.layout.item_wallpaper, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(this@WallpaperListActivity, 20f)
                , DP2PX.dip2px(this@WallpaperListActivity, 50f)))
            setOnItemClickListener { adapter, view, position ->
                ImageDialog(this@WallpaperListActivity,items[position].bodyUrl.split(",")).builder()
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@WallpaperListActivity.position=position
                val item=items[position]
                if (view.id==R.id.btn_download){
                    if (item.buyStatus==1){
                        val paintingBean=WallpaperDaoManager.getInstance().queryBean(item.contentId)
                        if (paintingBean==null){
                            onDownload()
                        }
                        else{
                            showToast("已下载")
                        }
                    }
                    else{
                        val map = HashMap<String, Any>()
                        map["type"] = 5
                        map["bookId"] = item.contentId
                        presenter.onBuy(map)
                    }
                }
            }
        }
    }

    /**
     * 下载
     */
    private fun onDownload(){
        val item=items[position]
        showLoading()
        val pathStr= FileAddress().getPathImage("wallpaper",item.contentId.toString())
        val images = mutableListOf(item.bodyUrl)
        val savePaths= arrayListOf("$pathStr/1.png")
        FileMultitaskDownManager.with(this).create(images).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    item.path=savePaths[0]
                    WallpaperDaoManager.getInstance().insertOrReplace(item)
                    showToast("下载完成")
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast("下载失败")
                }
            })
    }


    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["supply"]=supply
        map["type"]=1
        presenter.getList(map)
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

}