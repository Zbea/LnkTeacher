package com.bll.lnkteacher.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.presenter.TextbookPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list.*
import java.io.File

class TextbookFragment : BaseMainFragment(), IContractView.ITextbookView {

    private val mPresenter = TextbookPresenter(this,1)
    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var textBook = ""//用来区分课本类型
    private var tabId = 0
    private var position = 0

    override fun onAddHomeworkBook() {
        showToast("设置教辅书成功")
        books[position].isHomework = true
        mAdapter?.notifyItemChanged(position)
        BookGreenDaoManager.getInstance().insertOrReplaceBook(books[position])
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list
    }

    override fun initView() {
        super.initView()
        pageSize = 9
        setTitle(R.string.main_textbook_title)

        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
        if (NetworkUtil.isNetworkAvailable(requireActivity()))
            fetchCommonData()
    }

    private fun initTab() {
        val strs=DataBeanManager.textbookType
        textBook = strs[0]
        for (i in strs.indices){
            itemTabTypes.add(ItemTypeBean().apply {
                title=strs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        tabId = position
        textBook = itemTabTypes[position].title
        pageIndex = 1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),50f), DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 38))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val book = books[position]
            //教学教育跳转阅读器
            if (tabId == 0||tabId == 1||tabId == 4) {
                MethodManager.gotoBookDetails(requireActivity(), book)
            } else {
                MethodManager.gotoTextBookDetails(requireActivity(),book)
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                onLongClick(books[position])
                true
            }
    }


    //长按显示课本管理
    private fun onLongClick(book: Book) {
        //题卷本可以设置为作业
        val beans = mutableListOf<ItemList>()
        if (tabId == 3) {
            beans.add(ItemList().apply {
                name = "删除"
                resId = R.mipmap.icon_setting_delete
            })
            beans.add(ItemList().apply {
                name = "设置作业"
                resId = R.mipmap.icon_setting_delete
            })
        } else {
            beans.add(ItemList().apply {
                name = "删除"
                resId = R.mipmap.icon_setting_delete
            })
        }
        LongClickManageDialog(requireActivity(),1, book.bookName, beans).builder()
            .setOnDialogClickListener {
                if (it == 0) {
                    MethodManager.deleteBook(book,0)
                    mAdapter?.remove(position)
                } else {
                    if (!book.isHomework) {
                        val map = HashMap<String, Any>()
                        map["name"] = book.bookName
                        map["type"] = 2
                        map["subType"] = 4//题卷本
                        map["grade"] = book.grade
                        map["bookId"] = book.bookId
                        map["bgResId"] = book.imageUrl
                        mPresenter.addType(map)
                    } else {
                        showToast("请勿重复设置")
                    }
                }
            }
    }

    /**
     * 每天上传书籍
     */
    fun upload(tokenStr: String) {
        cloudList.clear()
        val maxBooks = mutableListOf<Book>()
        val books = BookGreenDaoManager.getInstance().queryAllTextBook()
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
                            type = 2
                            zipUrl = book.downloadUrl
                            downloadUrl = it
                            subTypeStr = book.subtypeStr
                            date = System.currentTimeMillis()
                            listJson = Gson().toJson(book)
                            bookId = book.bookId
                            bookTypeId = book.typeId
                        })
                        if (cloudList.size == maxBooks.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                cloudList.add(CloudListBean().apply {
                    type = 2
                    zipUrl = book.downloadUrl
                    subTypeStr = book.subtypeStr
                    date = System.currentTimeMillis()
                    listJson = Gson().toJson(book)
                    bookId = book.bookId
                    bookTypeId = book.typeId
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
            val bookBean = BookGreenDaoManager.getInstance().queryTextBookByBookID(item.bookTypeId, item.bookId)
            //删除书籍
            FileUtils.deleteFile(File(bookBean.bookPath))
            FileUtils.deleteFile(File(bookBean.bookDrawPath))
            BookGreenDaoManager.getInstance().deleteBook(bookBean)
        }
        fetchData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == TEXT_BOOK_EVENT) {
            fetchData()
        }
    }

    override fun fetchData() {
        books = BookGreenDaoManager.getInstance().queryAllTextBook(textBook, pageIndex, pageSize)
        val total = BookGreenDaoManager.getInstance().queryAllTextBook(textBook)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}