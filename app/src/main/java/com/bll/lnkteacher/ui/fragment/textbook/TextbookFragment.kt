package com.bll.lnkteacher.ui.fragment.textbook

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.book.TextbookBean
import com.bll.lnkteacher.mvp.presenter.TextbookPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TextbookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_list.rv_list

class TextbookFragment : BaseMainFragment(), IContractView.ITextbookView {

    private val mPresenter = TextbookPresenter(this,1)
    private var mAdapter: TextbookAdapter? = null
    private var books = mutableListOf<TextbookBean>()
    private var typeId = 0//用来区分课本类型
    private var position = 0

    override fun onAddHomeworkBook() {
        showToast(1,"设置教辅书成功")
        books[position].isHomework = true
        mAdapter?.notifyItemChanged(position)
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(books[position])
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(typeId:Int): TextbookFragment {
        val fragment= TextbookFragment()
        val bundle= Bundle()
        bundle.putInt("textbook",typeId)
        fragment.arguments=bundle
        return fragment
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        typeId= arguments?.getInt("textbook")!!
        pageSize = 9
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),40f), DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mAdapter = TextbookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco(3,  40))
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val book = books[position]
            if (typeId<2) {
                MethodManager.gotoTextBookDetails(requireActivity(),book)
            } else {
                MethodManager.gotoTeachingDetails(requireActivity(),book)
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                onLongClick()
                true
            }

    }


    //长按显示课本管理
    private fun onLongClick() {
        val book=books[position]
        //题卷本可以设置为作业
        val beans = mutableListOf<ItemList>()
        when(typeId){
            0->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
            }
            1->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
                beans.add(ItemList().apply {
                    name = "置顶"
                    resId = R.mipmap.icon_setting_top
                })
                beans.add(ItemList().apply {
                    name = "设为作业"
                    resId = R.mipmap.icon_setting_set
                })
            }
            else->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
                beans.add(ItemList().apply {
                    name = "置顶"
                    resId = R.mipmap.icon_setting_top
                })
            }
        }
        LongClickManageDialog(requireActivity(),1, book.bookName, beans).builder()
            .setOnDialogClickListener {
                when(it){
                    0->{
                        MethodManager.deleteTextbook(book)
                    }
                    1->{
                        book.time=System.currentTimeMillis()
                        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                        pageIndex=1
                        fetchData()
                    }
                    2->{
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
                            showToast(1,"已设置")
                        }
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
        books = TextbookGreenDaoManager.getInstance().queryAllTextBook(typeId, pageIndex, pageSize)
        val total = TextbookGreenDaoManager.getInstance().queryAllTextBook(typeId)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

}