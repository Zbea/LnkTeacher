package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.ui.activity.book.BookcaseTypeActivity
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.iv_content_down
import kotlinx.android.synthetic.main.fragment_bookcase.iv_content_up
import kotlinx.android.synthetic.main.fragment_bookcase.ll_book_top
import kotlinx.android.synthetic.main.fragment_bookcase.rv_list
import kotlinx.android.synthetic.main.fragment_bookcase.tv_book_type
import kotlinx.android.synthetic.main.fragment_bookcase.tv_name
import org.greenrobot.eventbus.EventBus

/**
 * 书架
 */
class BookcaseFragment : BaseMainFragment() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()//所有数据
    private var bookTopBean: Book? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(DataBeanManager.getIndexLeftData()[1].name)

        initRecyclerView()
        findBook()

        tv_book_type.setOnClickListener {
            startActivity(Intent(activity, BookcaseTypeActivity::class.java))
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

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.BOOK_EVENT->{
                findBook()
            }
            Constants.AUTO_REFRESH_EVENT->{
                mQiniuPresenter.getToken()
            }
        }
    }

    override fun onRefreshData() {
        findBook()
    }

    override fun onUpload(token: String) {
        cloudList.clear()
        val books = BookGreenDaoManager.getInstance().queryBookByHalfYear()
        for (book in books) {
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)) {
                FileUploadManager(token).apply {
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
                        if (cloudList.size == books.size)
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
                if (cloudList.size == books.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList) {
            val bookBean = BookGreenDaoManager.getInstance().queryBookByBookID(item.bookId)
            MethodManager.deleteBook(bookBean)
        }
        EventBus.getDefault().post(Constants.BOOK_EVENT)
    }

}