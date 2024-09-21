package com.bll.lnkteacher.ui.fragment.cloud

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseCloudFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_cloud_list_tab.rv_list
import java.io.File
import java.util.concurrent.CountDownLatch

class CloudBookcaseFragment:BaseCloudFragment() {

    private var countDownTasks: CountDownLatch?=null //异步完成后操作
    private var bookTypeStr=""
    private var mAdapter:BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_cloud_list_tab
    }

    override fun initView() {
        pageSize=12
        initRecyclerView()
    }

    override fun lazyLoad() {
        mCloudPresenter.getType(1)
    }

    //设置头部索引
    private fun initTab() {
        bookTypeStr=types[0]
        for (i in types.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title= types[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        bookTypeStr=types[position]
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(activity,28f),
            DP2PX.dip2px(activity,20f),
            DP2PX.dip2px(activity,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity,22f),50))
            setOnItemClickListener { adapter, view, position ->
                this@CloudBookcaseFragment.position=position
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
                this@CloudBookcaseFragment.position=position
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
    }

    private fun downloadItem(){
        val book=books[position]
        val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookPlusId)
        if (localBook == null) {
            showLoading()
            //判断书籍是否有手写内容，没有手写内容直接下载书籍zip
            if (!book.drawUrl.isNullOrEmpty()){
                countDownTasks= CountDownLatch(2)
                downloadBook(book)
                downloadBookDrawing(book)
            }else{
                countDownTasks= CountDownLatch(1)
                downloadBook(book)
            }
            downloadSuccess(book)
        } else {
            showToast("已下载")
        }
    }

    private fun deleteItem(){
        val ids= mutableListOf<Int>()
        ids.add(books[position].cloudId)
        mCloudPresenter.deleteCloud(ids)
    }

    /**
     * 下载完成
     */
    private fun downloadSuccess(book: Book){
        //等待两个请求完成后刷新列表
        Thread{
            countDownTasks?.await()
            requireActivity().runOnUiThread {
                hideLoading()
                val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookPlusId)
                if (localBook!=null){
                    deleteItem()
                    showToast(book.bookName+getString(R.string.book_download_success))
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
    private fun downloadBookDrawing(book: Book){
        val fileName = MD5Utils.digest(book.bookId.toString())//文件名
        val zipPath = FileAddress().getPathZip(fileName)
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
     * 下载书籍
     */
    private fun downloadBook(book: Book) {
        FileDownManager.with(activity).create(book.downloadUrl).setPath(book.bookPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    book.id=null
                    book.time=System.currentTimeMillis()
                    book.isLook=false
                    book.subtypeStr=""
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
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
        map["type"] = 1
        map["subTypeStr"] = bookTypeStr
        mCloudPresenter.getList(map)
    }

    override fun onCloudType(types: MutableList<String>) {
        types.remove("全部")
        types.add(0,"全部")
        this.types=types
        if (types.size>0)
            initTab()
    }

    override fun onCloudList(cloudList: CloudList) {
        setPageNumber(cloudList.total)
        books.clear()
        for (bookCloud in cloudList.list){
            if (bookCloud.listJson.isNotEmpty()){
                val bookBean= Gson().fromJson(bookCloud.listJson, Book::class.java)
                bookBean.id=null
                bookBean.cloudId=bookCloud.id
                bookBean.drawUrl=bookCloud.downloadUrl
                books.add(bookBean)
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onCloudDelete() {
        mAdapter?.remove(position)
    }
}