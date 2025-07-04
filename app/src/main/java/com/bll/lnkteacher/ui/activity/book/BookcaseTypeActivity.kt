package com.bll.lnkteacher.ui.activity.book

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.BOOK_EVENT
import com.bll.lnkteacher.Constants.Companion.BOOK_TYPE_EVENT
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.BookcaseDetailsDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.ui.adapter.BookcaseTypeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.ac_list_tab.rv_tab
import kotlinx.android.synthetic.main.common_title.tv_custom

/**
 * 书架分类
 */
class BookcaseTypeActivity : BaseAppCompatActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var tabPos=0
    private val mBookDaoManager=BookGreenDaoManager.getInstance()
    private var mTabAdapter: BookcaseTypeAdapter? = null

    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize = 12

        initTab()
    }

    override fun initView() {
        setPageTitle("分类展示")
        showView(tv_custom)

        tv_custom.text = "书架明细"
        tv_custom.setOnClickListener {
            BookcaseDetailsDialog(this).builder()
        }

        initRecycleView()

        fetchData()
    }

    private fun initTab() {
        itemTabTypes = ItemTypeDaoManager.getInstance().queryAll(2)
        if (itemTabTypes.size > 0) {
            for (item in itemTabTypes) {
                item.isCheck = false
            }
            itemTabTypes[0].isCheck = true
        }

        rv_tab.layoutManager = GridLayoutManager(this, 7)//创建布局管理
        mTabAdapter = BookcaseTypeAdapter(R.layout.item_bookcase_type, itemTabTypes).apply {
            rv_tab.adapter = this
            bindToRecyclerView(rv_tab)
            setOnItemClickListener { adapter, view, position ->
                itemTabTypes[tabPos].isCheck=false
                tabPos = position
                itemTabTypes[tabPos].isCheck = true
                //修改当前分类状态
                ItemTypeDaoManager.getInstance().saveBookBean( itemTabTypes[tabPos].title, false)
                notifyDataSetChanged()
                pageIndex = 1
                fetchData()
            }
        }
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex = 1
        tabPos=position
        fetchData()
    }

    private fun initRecycleView(){
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this, 30f), DP2PX.dip2px(this, 30f),
            DP2PX.dip2px(this, 30f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(4,  DP2PX.dip2px(this@BookcaseTypeActivity, 30f)))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                MethodManager.gotoBookDetails(this@BookcaseTypeActivity,books[position])
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                CommonDialog(this@BookcaseTypeActivity).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun ok() {
                        MethodManager.deleteBook(books[position])
                    }
                })
                true
            }
        }
    }

    private fun getTypeStr():String{
        return itemTabTypes[tabPos].title
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            BOOK_EVENT->{
                fetchData()
            }
            BOOK_TYPE_EVENT -> {
                itemTabTypes = ItemTypeDaoManager.getInstance().queryAll(2)
                itemTabTypes[tabPos].isCheck = true
                mTabAdapter?.setNewData(itemTabTypes)
            }
        }
    }

    override fun fetchData() {
        books=mBookDaoManager.queryAllBook(getTypeStr(), pageIndex, pageSize)
        val total = mBookDaoManager.queryAllBook(getTypeStr())
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}