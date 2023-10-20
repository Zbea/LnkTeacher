package com.bll.lnkteacher.ui.activity.book

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.BOOK_EVENT
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.*
import org.greenrobot.eventbus.EventBus

/**
 * 书架分类
 */
class BookListActivity : BaseActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var pos = 0 //当前书籍位置
    private val mBookDaoManager=BookGreenDaoManager.getInstance()
    private var longBeans = mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        longBeans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name="设置"
            resId=R.mipmap.icon_setting_set
        })
    }

    override fun initView() {
        pageSize = 12
        setPageTitle("书籍列表（未分类）")

        initRecycleView()
        fetchData()
    }

    private fun initRecycleView(){

        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,28f), DP2PX.dip2px(this,60f),DP2PX.dip2px(this,28f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(this@BookListActivity, 22f), DP2PX.dip2px(this@BookListActivity, 60f)))
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

    }

    //删除书架书籍
    private fun onLongClick() {
        val book=books[pos]
        LongClickManageDialog(this, book.bookName,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    deleteBook(book)
                    mAdapter?.remove(pos)
                }
                else{
                    val types=ItemTypeDaoManager.getInstance().queryAll(2)
                    val lists= mutableListOf<ItemList>()
                    for (i in types.indices){
                        lists.add(ItemList(i,types[i].title))
                    }
                    ItemSelectorDialog(this,"设置分类",lists).builder().setOnDialogClickListener{
                        val typeStr=types[it].title
                        book.subtypeStr=typeStr
                        mBookDaoManager.insertOrReplaceBook(book)
                        books.removeAt(pos)
                        mAdapter?.notifyItemChanged(pos)
                        EventBus.getDefault().post(BOOK_EVENT)
                    }
                }
            }
    }

    override fun fetchData() {
        books=mBookDaoManager.queryAllBook("", pageIndex, pageSize)
        val total = mBookDaoManager.queryAllBook("")
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }


}