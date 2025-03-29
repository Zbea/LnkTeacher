package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.presenter.CloudUploadPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ItemTypeManagerAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_list.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File

class ScreenshotManagerActivity : BaseAppCompatActivity() , IContractView.ICloudUploadView, IContractView.IQiniuView {

    private var mCloudUploadPresenter= CloudUploadPresenter(this)
    private var mQiniuPresenter= QiniuPresenter(this)
    private var items= mutableListOf<ItemTypeBean>()
    private var mAdapter: ItemTypeManagerAdapter? = null
    private var position=0

    override fun onToken(token: String) {
        uploadScreenShot(token)
    }
    override fun onSuccessCloudUpload(cloudIds: MutableList<Int>?) {
        showToast("上传成功")
        val item=items[position]
        FileUtils.deleteFile(File(item.path))
        ItemTypeDaoManager.getInstance().deleteBean(item)
        mAdapter?.remove(position)
        EventBus.getDefault().post(Constants.SCREENSHOT_MANAGER_EVENT)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        items= ItemTypeDaoManager.getInstance().queryAll(3)
    }

    override fun initView() {
        setPageTitle("管理截图分类")

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f),
            DP2PX.dip2px(this,100f),DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ItemTypeManagerAdapter(R.layout.item_notebook_manager, items).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                val item=items[position]
                when(view.id){
                    R.id.iv_edit->{
                        InputContentDialog(this@ScreenshotManagerActivity,item.title).builder().setOnDialogClickListener{
                            if (ItemTypeDaoManager.getInstance().isExist(it,1)){
                                showToast("已存在")
                                return@setOnDialogClickListener
                            }
                            val newPath= FileAddress().getPathScreen(it)
                            File(item.path).renameTo(File(newPath))
                            item.title=it
                            item.path=newPath
                            ItemTypeDaoManager.getInstance().insertOrReplace(item)
                            notifyItemChanged(position)
                        }
                    }
                    R.id.iv_delete->{
                        if (FileUtils.isExist(item.path)){
                            showToast("分类存在内容，无法删除")
                            return@setOnItemChildClickListener
                        }
                        CommonDialog(this@ScreenshotManagerActivity).setContent("确定删除？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                FileUtils.deleteFile(File(item.path))
                                ItemTypeDaoManager.getInstance().deleteBean(item)
                                EventBus.getDefault().post(Constants.SCREENSHOT_MANAGER_EVENT)
                                remove(position)
                            }
                        })
                    }
                    R.id.iv_top->{
                        val date=items[0].date
                        item.date=date-1000
                        ItemTypeDaoManager.getInstance().insertOrReplace(item)
                        items.sortWith(Comparator { item1, item2 ->
                            return@Comparator item1.date.compareTo(item2.date)
                        })
                        EventBus.getDefault().post(Constants.SCREENSHOT_MANAGER_EVENT)
                        notifyDataSetChanged()
                    }
                    R.id.iv_upload->{
                        CommonDialog(this@ScreenshotManagerActivity).setContent("确定上传？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                if (NetworkUtil.isNetworkConnected()){
                                    mQiniuPresenter.getToken()
                                }
                                else{
                                    showToast("网络连接失败")
                                }
                            }
                        })
                    }
                }
            }
        }

    }

    private fun uploadScreenShot(token:String){
        val cloudList= mutableListOf<CloudListBean>()
        val item=items[position]
        val fileName= DateUtils.longToString(item.date)
        val path=item.path
        if (FileUtils.isExistContent(path)){
            FileUploadManager(token).apply {
                startUpload(path,fileName)
                setCallBack{
                    cloudList.add(CloudListBean().apply {
                        type=6
                        subTypeStr="截图"
                        date=System.currentTimeMillis()
                        listJson= Gson().toJson(item)
                        downloadUrl=it
                    })
                    mCloudUploadPresenter.upload(cloudList)
                }
            }
        }
        else{
            showToast("暂无内容，无法上传")
        }
    }


}