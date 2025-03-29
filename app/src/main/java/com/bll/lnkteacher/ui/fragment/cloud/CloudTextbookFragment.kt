package com.bll.lnkteacher.ui.fragment.cloud

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseCloudFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.book.TextbookBean
import com.bll.lnkteacher.ui.adapter.TextbookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_list_tab.rv_list
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.CountDownLatch

class CloudTextbookFragment: BaseCloudFragment() {
    private var countDownTasks: CountDownLatch?=null //异步完成后操作
    private var mAdapter: TextbookAdapter?=null
    private var textbooks= mutableListOf<TextbookBean>()
    private var position=0
    private var tabPos=0
    private var textBook=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_list_tab
    }

    override fun initView() {
        pageSize=12
        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    //设置头部索引
    private fun initTab() {
        val texts= DataBeanManager.textbookType.toMutableList()
        texts.removeLast()
        textBook=texts[0]
        for (i in texts.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title= texts[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabPos=position
        textBook=itemTabTypes[tabPos].title
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,30f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = TextbookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                this@CloudTextbookFragment.position=position
                CommonDialog(requireActivity()).setContent("确定下载？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            downloadItem()
                        }
                    })
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@CloudTextbookFragment.position=position
                CommonDialog(requireActivity()).setContent("确定删除？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            deleteItem()
                        }
                    })
                true
            }
        }
        rv_list.addItemDecoration(SpaceGridItemDeco(3, 50))
    }

    private fun downloadItem(){
        val book=textbooks[position]
        val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(book.category,book.bookId)
        if (localBook == null) {
            showLoading()
            //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
            if (!book.drawUrl.isNullOrEmpty()){
                countDownTasks= CountDownLatch(2)
                selectBookOrZip(book)
                downloadBookDrawing(book)
            }
            else{
                countDownTasks=CountDownLatch(1)
                selectBookOrZip(book)
            }
            downloadSuccess(book)
        } else {
            showToast("已下载")
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(textbooks[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    private fun selectBookOrZip(book: TextbookBean){
        if (tabPos<2){
            downloadBook(book)
        }
        else{
            downloadTeachingBook(book)
        }
    }

    /**
     * 下载完成
     */
    private fun downloadSuccess(book: TextbookBean){
        //等待两个请求完成后刷新列表
        Thread{
            countDownTasks?.await()
            requireActivity().runOnUiThread {
                hideLoading()
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(book.category,book.bookId)
                if (localBook!=null){
                    showToast(book.bookName+getString(R.string.book_download_success))
                    deleteItem()
                    EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
                }
                else{
                    if (FileUtils.isExistContent(book.bookDrawPath)){
                        FileUtils.deleteFile(File(book.bookDrawPath))
                    }
                    if (FileUtils.isExistContent(book.bookPath)){
                        FileUtils.deleteFile(File(book.bookPath))
                    }
                    showToast(book.bookName+getString(R.string.book_download_fail))
                }
            }
            countDownTasks=null
        }.start()
    }

    /**
     * 下载书籍手写内容
     */
    private fun downloadBookDrawing(book: TextbookBean){
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(book.drawUrl))
        FileDownManager.with(activity).create(book.drawUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, book.bookDrawPath, object : IZipCallback {
                        override fun onFinish() {
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    /**
     * 下载课本
     */
    private fun downloadBook(book: TextbookBean) {
        val zipPath = FileAddress().getPathZip(FileUtils.getUrlName(book.downloadUrl))
        FileDownManager.with(activity).create(book.downloadUrl).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    ZipUtils.unzip(zipPath, book.bookPath, object : IZipCallback {
                        override fun onFinish() {
                            book.id=null
                            TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                            //删除教材的zip文件
                            FileUtils.deleteFile(File(zipPath))
                        }
                        override fun onProgress(percentDone: Int) {
                        }
                        override fun onError(msg: String?) {
                        }
                        override fun onStart() {
                        }
                    })
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    /**
     * 下载教学教育
     */
    private fun downloadTeachingBook(book: TextbookBean) {
        FileDownManager.with(activity).create(book.downloadUrl).setPath(book.bookPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    book.id=null
                    TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    countDownTasks?.countDown()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    countDownTasks?.countDown()
                }
            })
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"]=pageIndex
        map["size"] = pageSize
        map["type"] = 2
        map["subTypeStr"] = textBook
        mCloudPresenter.getList(map)
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        textbooks.clear()
        for (item in cloudList.list){
            if (item.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(item.listJson, TextbookBean::class.java)
                bookBean.id=null
                bookBean.cloudId=item.id
                bookBean.drawUrl=item.downloadUrl
                textbooks.add(bookBean)
            }
        }
        mAdapter?.setNewData(textbooks)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
        onRefreshList(textbooks)
    }

}