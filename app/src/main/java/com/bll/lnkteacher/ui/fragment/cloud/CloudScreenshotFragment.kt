package com.bll.lnkteacher.ui.fragment.cloud

import android.os.Handler
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseCloudFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.adapter.CloudScreenshotAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_list_type.*
import java.io.File

class CloudScreenshotFragment: BaseCloudFragment() {
    private var mAdapter: CloudScreenshotAdapter?=null
    private var items= mutableListOf<ItemTypeBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_list
    }

    override fun initView() {
        pageSize=20
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,50f), DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        mAdapter = CloudScreenshotAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudScreenshotFragment.position=position
                download(items[position])
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudScreenshotFragment.position=position
                if (view.id==R.id.iv_delete){
                    CommonDialog(requireActivity()).setContent("确定删除").builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                deleteItem()
                            }
                        })
                }
            }
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(items[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun download(item: ItemTypeBean){
        showLoading()
        val fileName= DateUtils.longToString(item.date)
        val zipPath = FileAddress().getPathZip(fileName)
        val fileTargetPath= FileAddress().getPathScreen(item.title)
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip1(zipPath, fileTargetPath, object : IZipCallback {
                        override fun onFinish() {
                            if (!ItemTypeDaoManager.getInstance().isExist(item.title,3)&&!item.title.equals("未分类")){
                                item.id=null//设置数据库id为null用于重新加入
                                ItemTypeDaoManager.getInstance().insertOrReplace(item)
                            }
                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            Handler().postDelayed({
                                showToast("下载成功")
                                deleteItem()
                                hideLoading()
                            },500)
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                            showToast(msg!!)
                            hideLoading()
                        }
                        override fun onStart() {
                        }
                    })
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast("下载失败")
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 6
        map["subTypeStr"]="截图"
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        items.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val itemTypeBean= Gson().fromJson(item.listJson, ItemTypeBean::class.java)
                itemTypeBean.cloudId=item.id
                itemTypeBean.downloadUrl=item.downloadUrl
                items.add(itemTypeBean)
            }
        }
        mAdapter?.setNewData(items)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }
}