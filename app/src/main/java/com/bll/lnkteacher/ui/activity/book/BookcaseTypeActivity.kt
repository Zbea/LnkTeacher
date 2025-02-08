package com.bll.lnkteacher.ui.activity.book

import PopupClick
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.BOOK_EVENT
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.BookcaseDetailsDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.common_title.iv_manager

/**
 * 书架分类
 */
class BookcaseTypeActivity : BaseActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var typeStr = ""//当前分类
    private var pos = 0 //当前书籍位置
    private val mBookDaoManager=BookGreenDaoManager.getInstance()
    private var popupBeans = mutableListOf<PopupBean>()
    private var longBeans = mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize = 12
        popupBeans.add(PopupBean(0,"创建分类", false))
        popupBeans.add(PopupBean(1, "删除分类", false))
        popupBeans.add(PopupBean(2, "书架明细", false))
    }

    override fun initView() {
        setPageTitle("分类展示")
        showView(iv_manager)

        iv_manager?.setOnClickListener {
            setTopSelectView()
        }

        initRecycleView()
        initTab()
    }

    private fun initTab() {
        itemTabTypes = ItemTypeDaoManager.getInstance().queryAll(2)
        itemTabTypes.add(0,ItemTypeBean().apply {
            title = "全部"
        })
        itemTabTypes[pos].isCheck=true
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        pageIndex = 1
        typeStr = itemTabTypes[position].title
        if (position == 0)
            typeStr = ""
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
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(this@BookcaseTypeActivity,1,bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                pos = position
                onLongClick()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,  DP2PX.dip2px(this, 30f)))
    }

    //顶部弹出选择
    private fun setTopSelectView() {
        PopupClick(this, popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
            when (item.id) {
                0 -> {
                    InputContentDialog(this,"创建分类").builder().setOnDialogClickListener{
                        if (ItemTypeDaoManager.getInstance().isExist(it,2)){
                            showToast("已存在")
                            return@setOnDialogClickListener
                        }
                        val bookTypeBean=ItemTypeBean()
                        bookTypeBean.type=2
                        bookTypeBean.date=System.currentTimeMillis()
                        bookTypeBean.title=it
                        ItemTypeDaoManager.getInstance().insertOrReplace(bookTypeBean)
                        mTabTypeAdapter?.addData(bookTypeBean)
                    }
                }
                1 -> {
                    val types=ItemTypeDaoManager.getInstance().queryAll(2)
                    val lists= mutableListOf<ItemList>()
                    for (i in types.indices){
                        lists.add(ItemList(i,types[i].title))
                    }
                    ItemSelectorDialog(this,"删除分类",lists).builder().setOnDialogClickListener{
                        val typeNameStr=types[it].title
                        val books = mBookDaoManager.queryAllBook(typeNameStr)
                        if (books.isNotEmpty()){
                            showToast("分类存在书籍，无法删除")
                            return@setOnDialogClickListener
                        }
                        ItemTypeDaoManager.getInstance().deleteBean(types[it])
                        var index = 0
                        for (i in itemTabTypes.indices) {
                            if (typeNameStr == itemTabTypes[i].title) {
                                index = i
                            }
                        }
                        mTabTypeAdapter?.remove(index)
                        if (typeStr==typeNameStr){
                            itemTabTypes[0].isCheck=true
                            typeStr=""
                            mTabTypeAdapter?.notifyItemChanged(0)
                            fetchData()
                        }
                    }
                }
                2->{
                    BookcaseDetailsDialog(this).builder()
                }
            }
        }
    }

    //删除书架书籍
    private fun onLongClick() {
        val book=books[pos]
        longBeans.clear()
        longBeans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        if (typeStr==""){
            longBeans.add(ItemList().apply {
                name="分类"
                resId=R.mipmap.icon_setting_set
            })
        }
        else{
            longBeans.add(ItemList().apply {
                name="移出"
                resId=R.mipmap.icon_setting_out
            })
        }

        LongClickManageDialog(this, book.bookName,longBeans).builder()
            .setOnDialogClickListener {
                if (it==0){
                    mAdapter?.remove(pos)
                    MethodManager.deleteBook(book,1)
                }
                else{
                    if (typeStr==""){
                        val types= ItemTypeDaoManager.getInstance().queryAll(2)
                        if (types.size==0){
                            showToast("请先创建分类")
                            return@setOnDialogClickListener
                        }
                        val lists= mutableListOf<ItemList>()
                        for (i in types.indices){
                            lists.add(ItemList(i,types[i].title))
                        }
                        ItemSelectorDialog(this,"设置分类",lists).builder().setOnDialogClickListener{
                            val typeStr=types[it].title
                            book.subtypeStr=typeStr
                            mBookDaoManager.insertOrReplaceBook(book)
                            fetchData()
                        }
                    }
                    else{
                        book.subtypeStr=""
                        mBookDaoManager.insertOrReplaceBook(book)
                        fetchData()
                    }
                }
            }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }

    override fun fetchData() {
        books=mBookDaoManager.queryAllBook(typeStr, pageIndex, pageSize)
        val total = mBookDaoManager.queryAllBook(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}