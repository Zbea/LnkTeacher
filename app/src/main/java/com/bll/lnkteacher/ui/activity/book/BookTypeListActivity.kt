package com.bll.lnkteacher.ui.activity.book

import PopupClick
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.BOOK_EVENT
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.ac_book_type_list.*
import kotlinx.android.synthetic.main.common_title.*

/**
 * 书架分类
 */
class BookTypeListActivity : BaseActivity() {

    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var typeStr = ""//当前分类
    private var pos = 0 //当前书籍位置
    private var bookTypes= mutableListOf<ItemTypeBean>()
    private var popupBeans = mutableListOf<PopupBean>()
    private var longBeans = mutableListOf<ItemList>()

    override fun layoutId(): Int {
        return R.layout.ac_book_type_list
    }

    override fun initData() {
        pageSize = 12
        popupBeans.add(PopupBean(0, "创建分类", false))
        popupBeans.add(PopupBean(1, "删除分类", false))

        longBeans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        longBeans.add(ItemList().apply {
            name="移出"
            resId=R.mipmap.icon_setting_out
        })

    }

    override fun initView() {
        setPageTitle("分类展示")
        showView(tv_custom,tv_province)

        tv_custom.text="书籍列表"
        tv_province.text="分类管理"

        tv_province?.setOnClickListener {
            setTopSelectView()
        }

        tv_custom?.setOnClickListener {
            startActivity(Intent(this,BookListActivity::class.java))
        }

        initRecycleView()
        initTab()
    }

    //顶部弹出选择
    private fun setTopSelectView() {
        PopupClick(this, popupBeans, tv_province,tv_province.width, 5).builder().setOnSelectListener { item ->
            when (item.id) {
                0 -> {
                    InputContentDialog(this,"创建书籍分类").builder().setOnDialogClickListener{
                        if (ItemTypeDaoManager.getInstance().isExist(it,2)){
                            showToast("已存在")
                            return@setOnDialogClickListener
                        }
                        val bookTypeBean= ItemTypeBean()
                        bookTypeBean.type=2
                        bookTypeBean.date=System.currentTimeMillis()
                        bookTypeBean.title=it
                        ItemTypeDaoManager.getInstance().insertOrReplace(bookTypeBean)

                        rg_group.addView(getRadioButton(bookTypes.size, it, bookTypes.size==0))
                        bookTypes.add(bookTypeBean)
                        //更新tab
                        if (bookTypes.isEmpty()){
                            typeStr=it
                            fetchData()
                        }
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
                        val books = BookGreenDaoManager.getInstance().queryAllBook(typeNameStr)
                        if (books.size>0){
                            showToast("分类存在书籍，无法删除")
                            return@setOnDialogClickListener
                        }
                        ItemTypeDaoManager.getInstance().deleteBean(types[it])
                        var index=0
                        for (i in bookTypes.indices){
                            if (typeNameStr == bookTypes[i].title){
                                index=i
                            }
                        }
                        rg_group.removeViewAt(index)
                        if (typeStr==typeNameStr){
                            if (bookTypes.size>0){
                                rg_group.check(0)
                            }
                            else{
                                books.clear()
                                mAdapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initTab() {
        bookTypes = ItemTypeDaoManager.getInstance().queryAll(2)
        if (bookTypes.isEmpty()){
            return
        }
        rg_group.removeAllViews()
        typeStr = bookTypes[0].title
        for (i in bookTypes.indices) {
            rg_group.addView(getRadioButton(i, bookTypes[i].title, i==0))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            pageIndex = 1
            typeStr=bookTypes[id].title
            fetchData()
        }

        fetchData()
    }

    private fun initRecycleView(){
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_bookstore, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list?.addItemDecoration(
                SpaceGridItemDeco1(4, DP2PX.dip2px(this@BookTypeListActivity, 22f), DP2PX.dip2px(this@BookTypeListActivity, 35f))
            )
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(this@BookTypeListActivity,bookBean)
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
                    mAdapter?.remove(pos)
                    deleteBook(book)
                }
                else{
                    book.subtypeStr=""
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    mAdapter?.remove(pos)
                }
            }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == BOOK_EVENT) {
            fetchData()
        }
    }

    override fun fetchData() {
        if (bookTypes.isEmpty()){
            return
        }
        books=BookGreenDaoManager.getInstance().queryAllBook(typeStr, pageIndex, pageSize)
        val total = BookGreenDaoManager.getInstance().queryAllBook(typeStr)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }


}