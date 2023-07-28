package com.bll.lnkteacher.ui.activity.book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.BookManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.ui.adapter.BookCaseTypeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 书架分类
 */
class BookTypeListActivity : BaseActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var typePos = 0
    private var typeStr = ""//当前分类
    private var pos = 0 //当前书籍位置
    private var bookNameStr = ""

    override fun layoutId(): Int {
        return R.layout.ac_book_type_list
    }

    override fun initData() {
    }

    override fun initView() {
        pageSize = 12

        setPageTitle(R.string.book_type_title)
        showSearchView(true)

        et_search.addTextChangedListener {
            bookNameStr = it.toString()
            if (bookNameStr.isNotEmpty()) {
                pageIndex = 1
                fetchData()
            }
        }

        initTab()

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_book_type, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_book_empty)
            rv_list?.addItemDecoration(
                SpaceGridItemDeco1(
                    4,
                    DP2PX.dip2px(this@BookTypeListActivity, 22f),
                    DP2PX.dip2px(this@BookTypeListActivity, 35f)
                )
            )
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                gotoBookDetails(bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                    pos = position
                    onLongClick()
                    true
                }
        }

        fetchData()
    }

    //设置tab
    @SuppressLint("NotifyDataSetChanged")
    private fun initTab() {
        val types = mutableListOf<ItemList>()
        val strings = DataBeanManager.bookType
        for (i in strings.indices) {
            val item = ItemList()
            item.name = strings[i]
            item.isCheck = i == 0
            types.add(item)
        }
        typeStr = types[0].name

        rv_type.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        BookCaseTypeAdapter(R.layout.item_bookcase_type, types).apply {
            rv_type.adapter = this
            bindToRecyclerView(rv_type)
            rv_type.addItemDecoration(SpaceGridItemDeco1(7, DP2PX.dip2px(this@BookTypeListActivity, 14f)
                , DP2PX.dip2px(this@BookTypeListActivity, 16f)))
            setOnItemClickListener { adapter, view, position ->
                getItem(typePos)?.isCheck = false
                typePos = position
                getItem(typePos)?.isCheck = true
                typeStr = types[typePos].name
                notifyDataSetChanged()
                bookNameStr = ""//清除搜索标记
                pageIndex = 1
                fetchData()
            }
        }
    }

    /**
     * 跳转阅读器
     */
    private fun gotoBookDetails(bookBean: Book){
        bookBean.isLook=true
        bookBean.time=System.currentTimeMillis()
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean)
        EventBus.getDefault().post(BOOK_EVENT)
        val intent = Intent()
        intent.action = "com.geniatech.reader.action.VIEW_BOOK_PATH"
        intent.setPackage("com.geniatech.knote.reader")
        intent.putExtra("path", bookBean.bookPath)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 1)
        startActivity(intent)
    }

    //删除书架书籍
    private fun onLongClick() {
        val book = books[pos]
        BookManageDialog(this, book,1).builder()
            .setOnDialogClickListener (object : BookManageDialog.OnDialogClickListener {
                override fun onDelete() {
                    BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                    FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                    if (File(book.bookDrawPath).exists())
                        FileUtils.deleteFile(File(book.bookDrawPath))
                    books.remove(book)
                    mAdapter?.notifyDataSetChanged()
                    EventBus.getDefault().post(BOOK_EVENT)
                }
                override fun onSet() {
                }
            })
    }


    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }


    override fun fetchData() {
        hideKeyboard()
        val total: MutableList<Book>
        //判断是否是搜索
        if (bookNameStr.isEmpty()) {
            books = BookGreenDaoManager.getInstance()
                .queryAllBook(typeStr, pageIndex, pageSize)
            total = BookGreenDaoManager.getInstance().queryAllBook(typeStr)
        } else {
            books = BookGreenDaoManager.getInstance()
                .queryBookByName(bookNameStr, typeStr, pageIndex, pageSize)
            total = BookGreenDaoManager.getInstance().queryBookByName(bookNameStr, typeStr)
        }

        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }


}