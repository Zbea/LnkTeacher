package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.AppList
import com.bll.lnkteacher.mvp.presenter.AppCenterPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.AppCenterListAdapter
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_download_app.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class AppCenterActivity:BaseActivity(), IContractView.IAPPView{

    private var presenter= AppCenterPresenter(this)
    private var type=1
    private var mAdapter:AppCenterListAdapter?=null
    private var apps= mutableListOf<AppList.ListBean>()
    private var app: AppList.ListBean?=null
    private var currentDownLoadTask:BaseDownloadTask?=null

    override fun onAppList(appBean: AppList) {
        setPageNumber(appBean.total)
        apps=appBean.list
        mAdapter?.setNewData(apps)
    }

    override fun buySuccess() {
        app?.buyStatus=1
        mAdapter?.notifyDataSetChanged()

        if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
            currentDownLoadTask = downLoadStart(app!!)
        } else {
            showToast(R.string.toast_download_install)
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_download_app
    }

    override fun initData() {
        pageSize=8
        fetchData()
    }

    override fun initView() {
        setPageTitle("应用中心")

        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            type = if (id==R.id.rb_official){
                1
            }else{
                2
            }
            pageIndex=1
            fetchData()
        }

        initRecyclerView()

    }

    private fun initRecyclerView(){
        val layoutParams=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,52f),DP2PX.dip2px(this,50f),DP2PX.dip2px(this,52f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = AppCenterListAdapter(R.layout.item_app_center_list, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                app=apps[position]
                if (app?.buyStatus==0){
                    val map = HashMap<String, Any>()
                    map["type"] = 4
                    map["bookId"] = app?.applicationId!!
                    presenter.buyApk(map)
                }
                else{
                    if (!isInstalled()) {
                        if (currentDownLoadTask == null || !currentDownLoadTask!!.isRunning) {
                            currentDownLoadTask = downLoadStart(app!!)
                        } else {
                            showToast(R.string.toast_download_install)
                        }
                    }
                }
            }
        }

    }


    //下载应用
    private fun downLoadStart(bean: AppList.ListBean): BaseDownloadTask? {

        val targetFileStr= FileAddress().getPathApk(bean.applicationId.toString())

        //看看 是否已经下载
        val listFiles = FileUtils.getFiles(Constants.APK_PATH)
        for (file in listFiles) {
            if (file.name.contains( bean.applicationId.toString())) {
                //已经下载 直接去解析apk 去安装
                installApk(targetFileStr)
                return null
            }
        }
        showLoading()
        val download = FileDownManager.with(this).create(bean.contentUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
            FileDownManager.SingleTaskCallBack {

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }
            override fun completed(task: BaseDownloadTask?) {
                hideLoading()
                installApk(targetFileStr)
                currentDownLoadTask = null//完成了废弃线程
            }
            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                showToast(e!!.message!!)
            }
        })
        return download
    }

    //安装apk
    private fun installApk(apkPath: String) {
        AppUtils.installApp(this, apkPath)
        EventBus.getDefault().post(Constants.APP_EVENT)
    }

    //是否已经下载安装
    private fun isInstalled(): Boolean {
        val listFile = FileUtils.getFiles(File(Constants.APK_PATH).path)
        if (listFile.size > 0) {
            for (file in listFile) {
                if (file.name.contains("" + app?.applicationId)) {//证明已经下载
                    val packageName = AppUtils.getApkInfo(this, FileAddress().getPathApk(app?.applicationId.toString()))
                    try {
                        AppUtils.startAPP(this, packageName)
                        return true
                    } catch (_: Exception) {
                    }
                }
            }
        }

        return false
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["type"] = type
        presenter.getAppList(map)
    }

}