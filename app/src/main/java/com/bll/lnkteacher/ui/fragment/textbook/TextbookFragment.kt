package com.bll.lnkteacher.ui.fragment.textbook

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.presenter.TextbookPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_list.rv_list

class TextbookFragment : BaseMainFragment(), IContractView.ITextbookView {

    private val mPresenter = TextbookPresenter(this,1)
    private var mAdapter: BookAdapter? = null
    private var books = mutableListOf<Book>()
    private var textBook = ""//用来区分课本类型
    private var position = 0

    override fun onAddHomeworkBook() {
        showToast(1,"设置教辅书成功")
        books[position].isHomework = true
        mAdapter?.notifyItemChanged(position)
        BookGreenDaoManager.getInstance().insertOrReplaceBook(books[position])
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(index:String): TextbookFragment {
        val fragment= TextbookFragment()
        val bundle= Bundle()
        bundle.putString("textbook",index)
        fragment.arguments=bundle
        return fragment
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        textBook= arguments?.getString("textbook")!!
        pageSize = 9
        initRecyclerView()
    }

    override fun lazyLoad() {
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
        rv_list?.addItemDecoration(SpaceGridItemDeco1(3, DP2PX.dip2px(activity, 33f), 50))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val book = books[position]
            if (textBook==DataBeanManager.textbookType[2]||textBook==DataBeanManager.textbookType[3]) {
                MethodManager.gotoTextBookDetails(requireActivity(),book)
            } else {
                MethodManager.gotoBookDetails(requireActivity(),2, book)
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
        if (textBook==DataBeanManager.textbookType[2]||textBook==DataBeanManager.textbookType[3]) {
            beans.add(ItemList().apply {
                name = "删除"
                resId = R.mipmap.icon_setting_delete
            })
            beans.add(ItemList().apply {
                name = "设置作业"
                resId = R.mipmap.icon_setting_set
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
                        showToast(1,"请勿重复设置")
                    }
                }
            }
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