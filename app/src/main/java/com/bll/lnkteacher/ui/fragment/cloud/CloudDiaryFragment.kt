package com.bll.lnkteacher.ui.fragment.cloud

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseCloudFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.DiaryDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.DiaryBean
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.adapter.CloudDiaryAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.rv_list
import java.io.File

class CloudDiaryFragment: BaseCloudFragment() {
    private var mAdapter: CloudDiaryAdapter?=null
    private var items= mutableListOf<CloudListBean>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=14
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(activity,30f), DP2PX.dip2px(activity,20f), DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = CloudDiaryAdapter(R.layout.item_cloud_diary, null).apply {
            rv_list.layoutManager = LinearLayoutManager(activity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudDiaryFragment.position=position
                CommonDialog(requireActivity()).setContent("确定下载？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            showLoading()
                            val item=items[position]
                            if (!ItemTypeDaoManager.getInstance().isExistDiaryType(item.id)){
                                showLoading()
                                download(item)
                            }
                            else{
                                showToast(R.string.toast_downloaded)
                            }
                        }
                    })
            }
            setOnItemChildClickListener { adapter, view, position ->
                this@CloudDiaryFragment.position=position
                if (view.id==R.id.iv_delete){
                    CommonDialog(requireActivity()).setContent("确定删除？").builder()
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
        rv_list.addItemDecoration(SpaceItemDeco(30))
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(items[position].id)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun download(item:CloudListBean){
        val fileName=DateUtils.longToStringCalender(item.date)
        val zipPath = FileAddress().getPathZip(fileName)
        val fileTargetPath= File(FileAddress().getPathDiary(fileName)).parent
        FileDownManager.with(activity).create(item.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object :
                FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, fileTargetPath, object : IZipCallback {
                        override fun onFinish() {
                            val itemTypeBean= ItemTypeBean().apply {
                                type=4
                                title=item.title
                                date=System.currentTimeMillis()
                                typeId=item.id
                            }
                            ItemTypeDaoManager.getInstance().insertOrReplace(itemTypeBean)

                            val diaryBeans: MutableList<DiaryBean> = Gson().fromJson(item.listJson, object : TypeToken<List<DiaryBean>>() {}.type)
                            for (diaryBean in diaryBeans){
                                diaryBean.id=null//设置数据库id为null用于重新加入
                                diaryBean.isUpload=true
                                diaryBean.uploadId=item.id
                                DiaryDaoManager.getInstance().insertOrReplace(diaryBean)
                            }

                            //删掉本地zip文件
                            FileUtils.deleteFile(File(zipPath))
                            showToast("下载成功")
                            hideLoading()
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
        map["type"] = 4
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        items=cloudList.list
        mAdapter?.setNewData(items)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(items)
    }
}