package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.BookManageDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.HandoutsList
import com.bll.lnkteacher.mvp.presenter.TextbookPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.BookDetailsActivity
import com.bll.lnkteacher.ui.activity.book.BookStoreTypeActivity
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.ui.adapter.HandoutsAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class TextbookFragment : BaseFragment() , IContractView.ITextbookView {

    private val mPresenter=TextbookPresenter(this)
    private var mAdapter: BookAdapter? = null
    private var mHandoutsAdapter:HandoutsAdapter?=null
    private var books = mutableListOf<Book>()
    private var textBook = ""//用来区分课本类型
    private var tabId=0
    private var position = 0
    private var handoutsBeans= mutableListOf<HandoutsList.HandoutsBean>()

    override fun onHandoutsList(list: HandoutsList) {
        setPageNumber(list.total)
        handoutsBeans = list.list
        mHandoutsAdapter?.setNewData(handoutsBeans)
    }

    override fun onAddHomeworkBook() {
        showToast("设置题卷本成功")
        books[position].isHomework=true
        mAdapter?.notifyItemChanged(position)
        BookGreenDaoManager.getInstance().insertOrReplaceBook(books[position])
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        pageSize=12
        setTitle(R.string.main_textbook_title)
        showSearch(true)

        ll_search?.setOnClickListener {
            startActivity(Intent(activity, BookStoreTypeActivity::class.java))
        }

        initTab()
        initRecyclerView()
        initRecyclerHandouts()

        fetchData()
    }

    override fun lazyLoad() {
        fetchCommonData()
    }

    //设置头部索引
    private fun initTab() {
        val tabStrs = DataBeanManager.textbookType
        textBook=tabStrs[0]
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i, tabStrs[i], tabStrs.size - 1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            tabId=id
            textBook = tabStrs[id]
            pageIndex=1
            if (tabId==5){
                showView(rv_handouts,tv_grade)
                disMissView(rv_list)
            }
            else{
                showView(rv_list)
                disMissView(rv_handouts,tv_grade)
            }
            fetchData()
        }
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(3,DP2PX.dip2px(activity, 33f), 38))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val book=books[position]
            //教学教育跳转阅读器
            if (tabId==4){
                gotoBookDetails(book)
            }
            else{
                val intent = Intent(activity, BookDetailsActivity::class.java)
                intent.putExtra("book_id", book.bookId)
                intent.putExtra("book_type", book.typeId)
                startActivity(intent)
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                onLongClick(books[position])
                true
            }
    }

    private fun initRecyclerHandouts(){
        rv_handouts.layoutManager = GridLayoutManager(requireActivity(), 3)//创建布局管理
        mHandoutsAdapter = HandoutsAdapter(R.layout.item_textbook, null).apply {
            rv_handouts.adapter = this
            bindToRecyclerView(rv_handouts)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val item=handoutsBeans[position]
                val images=item.paths.split(",")
                ImageDialog(requireActivity(),images).builder()
            }
        }
    }

    //长按显示课本管理
    private fun onLongClick(book: Book) {
        //题卷本可以设置为作业
        val type=if (tabId==2||tabId==3) 2 else 1
        BookManageDialog(requireActivity(), book,type).builder()
            .setOnDialogClickListener (object : BookManageDialog.OnDialogClickListener {
                override fun onDelete() {
                    BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                    FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                    FileUtils.deleteFile(File(book.bookDrawPath))
                    mAdapter?.remove(position)
                    EventBus.getDefault().post(TEXT_BOOK_EVENT)
                }
                override fun onSet() {
                    if (!book.isHomework){
                        val map=HashMap<String,Any>()
                        map["name"]=book.bookName
                        map["type"]=2
                        map["subType"]=4//题卷本
                        map["grade"]=grade
                        map["bookId"]=book.bookId
                        mPresenter.addType(map)
                    }
                }
            })
    }


    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == TEXT_BOOK_EVENT) {
            fetchData()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchCommonData()
    }

    override fun fetchData() {
        if (tabId==5){
            val map=HashMap<String,Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"]=grade
            mPresenter.getList(map)
        }
        else{
            books = BookGreenDaoManager.getInstance().queryAllTextBook( textBook, pageIndex, 9)
            val total = BookGreenDaoManager.getInstance().queryAllTextBook( textBook)
            setPageNumber(total.size)
            mAdapter?.setNewData(books)
        }
    }

    override fun onGradeEvent() {
        super.onGradeEvent()
        pageIndex=1
        fetchData()
    }

}