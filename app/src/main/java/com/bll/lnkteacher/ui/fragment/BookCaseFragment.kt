package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.ui.activity.book.BookTypeListActivity
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.*
import java.io.File

/**
 * 书架
 */
class BookCaseFragment : BaseMainFragment() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()//所有数据
    private var bookTopBean: Book? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(R.string.main_bookcase_title)

        initRecyclerView()
        findBook()

        tv_type.setOnClickListener {
            startActivity(Intent(activity, BookTypeListActivity::class.java))
        }

        ll_book_top.setOnClickListener {
            bookTopBean?.let { MethodManager.gotoBookDetails(requireActivity(), it) }
        }
    }

    override fun lazyLoad() {
    }


    private fun initRecyclerView() {
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity, 4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(activity, 22f), 30))
            setOnItemClickListener { adapter, view, position ->
                val bookBean = books[position]
                MethodManager.gotoBookDetails(requireActivity(), bookBean)
            }
        }
    }


    /**
     * 查找本地书籍
     */
    private fun findBook() {
        books = BookGreenDaoManager.getInstance().queryAllBook(true)
        if (books.size == 0) {
            bookTopBean = null
        } else {
            bookTopBean = books[0]
            books.removeFirst()
        }
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView() {
        if (bookTopBean != null) {
            setImageUrl(bookTopBean?.imageUrl!!, iv_content_up)
            setImageUrl(bookTopBean?.imageUrl!!, iv_content_down)
            tv_name.text = bookTopBean?.bookName
        } else {
            iv_content_up.setImageResource(0)
            iv_content_down.setImageResource(0)
            tv_name.text = ""
        }
    }


    private fun setImageUrl(url: String, image: ImageView) {
        GlideUtils.setImageRoundUrl(activity, url, image, 5)
    }

    /**
     * 每天上传书籍
     */
    fun upload(tokenStr: String) {
        cloudList.clear()
        val maxBooks = mutableListOf<Book>()
        val books = BookGreenDaoManager.getInstance().queryAllBook()
        //遍历获取所有需要上传的书籍数目
        for (item in books) {
            if (System.currentTimeMillis() >= item.time + Constants.halfYear) {
                maxBooks.add(item)
            }
        }
        for (book in maxBooks) {
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)) {
                FileUploadManager(tokenStr).apply {
                    startUpload(book.bookDrawPath, book.bookId.toString())
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 1
                            zipUrl = book.downloadUrl
                            downloadUrl = it
                            subTypeStr = book.subtypeStr.ifEmpty { "全部" }
                            date = System.currentTimeMillis()
                            listJson = Gson().toJson(book)
                            bookId = book.bookId
                        })
                        if (cloudList.size == maxBooks.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                cloudList.add(CloudListBean().apply {
                    type = 1
                    zipUrl = book.downloadUrl
                    subTypeStr = book.subtypeStr.ifEmpty { "全部" }
                    date = System.currentTimeMillis()
                    listJson = Gson().toJson(book)
                    bookId = book.bookId
                })
                if (cloudList.size == maxBooks.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    //上传完成后删除书籍
    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList) {
            val bookBean = BookGreenDaoManager.getInstance().queryBookByBookID(item.bookId)
            //删除书籍
            FileUtils.deleteFile(File(bookBean.bookPath))
            FileUtils.deleteFile(File(bookBean.bookDrawPath))
            BookGreenDaoManager.getInstance().deleteBook(bookBean)
        }
        findBook()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.BOOK_EVENT) {
            findBook()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        findBook()
    }

}