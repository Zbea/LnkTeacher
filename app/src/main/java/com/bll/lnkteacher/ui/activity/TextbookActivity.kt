package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.ui.adapter.BookAdapter
import kotlinx.android.synthetic.main.ac_homework_assgin_content.*

class TextbookActivity:BaseActivity() {

    private var mAdapter:BookAdapter?=null
    private var items= mutableListOf<Book>()

    override fun layoutId(): Int {
        return R.layout.ac_textbook
    }

    override fun initData() {

        for (i in 0..6){
            val book= Book()
            book.bookName= "数学$i"
            items.add(book)
        }

    }

    override fun initView() {
        setPageTitle("我的讲义")

        rv_list.layoutManager = GridLayoutManager(this,4)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, items)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)

    }
}