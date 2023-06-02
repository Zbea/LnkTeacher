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
import com.bll.lnkteacher.mvp.model.HandoutsList
import com.bll.lnkteacher.mvp.presenter.HandoutsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.BookDetailsActivity
import com.bll.lnkteacher.ui.activity.BookStoreActivity
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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class TextbookFragment : BaseFragment() ,IContractView.IHandoutsView{

    private val mPresenter=HandoutsPresenter(this)
    private var mAdapter: BookAdapter? = null
    private var mHandoutsAdapter:HandoutsAdapter?=null
    private var books = mutableListOf<Book>()
    private var textBook = ""//用来区分课本类型
    private var tabId=0
    private var position = 0
    private var book: Book? = null
    private var handoutsBeans= mutableListOf<HandoutsList.HandoutsBean>()

    override fun onList(list: HandoutsList) {
        setPageNumber(list.total)
        handoutsBeans = list.list
        mHandoutsAdapter?.setNewData(handoutsBeans)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        pageSize=12

        EventBus.getDefault().register(this)
        setTitle(R.string.main_textbook_title)
        showSearch(true)

        tv_search?.setOnClickListener {
            startActivity(Intent(activity, BookStoreActivity::class.java))
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
            if (tabId==3){
                showView(rv_handouts,tv_fragment_grade)
                disMissView(rv_list)
            }
            else{
                showView(rv_list)
                disMissView(rv_handouts,tv_fragment_grade)
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
            val intent = Intent(activity, BookDetailsActivity::class.java)
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
                CommonDialog(requireActivity()).setContent(R.string.textbook_is_delete_tips).builder()
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

    private fun initRecyclerHandouts(){
        rv_handouts.layoutManager = GridLayoutManager(requireActivity(), 3)//创建布局管理
        mHandoutsAdapter = HandoutsAdapter(R.layout.item_textbook, null).apply {
            rv_handouts.adapter = this
            bindToRecyclerView(rv_handouts)
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->

            }
        }
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == TEXT_BOOK_EVENT) {
            fetchData()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        fetchCommonData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun fetchData() {
        if (tabId==3){
            val map=HashMap<String,Any>()
            map["page"] = pageIndex
            map["size"] = pageSize
            map["grade"]=grade
            mPresenter.getList(map)
        }
        else{
            books = BookGreenDaoManager.getInstance().queryAllTextBook(0, textBook, pageIndex, 9)
            val total = BookGreenDaoManager.getInstance().queryAllTextBook(0, textBook)
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