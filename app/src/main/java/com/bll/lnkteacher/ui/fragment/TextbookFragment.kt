package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.BookManageDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.ui.activity.BookDetailsActivity
import com.bll.lnkteacher.ui.activity.BookStoreActivity
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import kotlin.math.ceil

class TextbookFragment : BaseFragment() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var textBook = "我的课本"//用来区分课本类型
    private var position = 0
    private var book: Book? = null
    private var pageIndex = 1
    private var pageTotal = 1


    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("教义")
        showSearch(true)

        initTab()
        initRecyclerView()

        tvSearch?.setOnClickListener {
            startActivity(Intent(activity, BookStoreActivity::class.java))
        }

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                findData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageTotal){
                pageIndex+=1
                findData()
            }
        }

        findData()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {
        var tabStrs = DataBeanManager.textbookType
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i, tabStrs[i], tabStrs.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            textBook = tabStrs[id]
            pageIndex=1
            findData()
        }
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(activity, 33f), 38))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(activity, BookDetailsActivity::class.java)
            intent.putExtra("book_id", books[position].bookId)
            startActivity(intent)
        }

        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                book = books[position]
                onLongClick()
            }

    }

    //长按显示课本管理
    private fun onLongClick(): Boolean {
        BookManageDialog(requireActivity(), book!!).builder()
            .setOnDialogClickListener {
                CommonDialog(activity).setContent("确认删除该教材？").builder()
                    .setDialogClickListener(object :
                        CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }

                        override fun ok() {
                            BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                            FileUtils.deleteFile(File(book?.bookPath))//删除下载的书籍资源
                            books.remove(book)
                            mAdapter?.notifyDataSetChanged()
                            EventBus.getDefault().post(TEXT_BOOK_EVENT)
                        }
                    })
            }
        return true
    }


    /**
     * 查找本地课本
     */
    private fun findData() {
        books = BookGreenDaoManager.getInstance().queryAllTextBook(0, textBook, pageIndex, 9)
        val total = BookGreenDaoManager.getInstance().queryAllTextBook(0, textBook)
        pageTotal = ceil((total.size.toDouble() / 9)).toInt()

        mAdapter?.setNewData(books)
        tv_page_current.text = pageIndex.toString()
        tv_page_total.text = pageTotal.toString()
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == TEXT_BOOK_EVENT) {
            findData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}