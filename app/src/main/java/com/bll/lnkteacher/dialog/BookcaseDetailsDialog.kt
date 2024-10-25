package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.ItemTypeDaoManager
import com.bll.lnkteacher.mvp.model.book.BookcaseDetailsBean
import com.bll.lnkteacher.ui.adapter.BookcaseDetailsAdapter
import com.bll.lnkteacher.widget.MaxRecyclerView
import com.bll.lnkteacher.widget.SpaceItemDeco

class BookcaseDetailsDialog(val context: Context) {

    fun builder(): BookcaseDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_bookcase_list)
        val window= dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val total=BookGreenDaoManager.getInstance().queryAllBook().size

        val tv_total=dialog.findViewById<TextView>(R.id.tv_book_total)
        tv_total.text="总计：${total}本"

        val items= mutableListOf<BookcaseDetailsBean>()
        val item =BookcaseDetailsBean()
        item.typeStr="全部"
        val books= BookGreenDaoManager.getInstance().queryAllBook("")
        if (books.size>0){
            item.num=books.size
            item.books=books
            items.add(item)
        }

        val itemTypes= ItemTypeDaoManager.getInstance().queryAll(2)
        for (itemTypeBean in itemTypes){
            val bookcaseDetailsBean =BookcaseDetailsBean()
            bookcaseDetailsBean.typeStr=itemTypeBean.title
            val books= BookGreenDaoManager.getInstance().queryAllBook(itemTypeBean.title)
            if (books.size>0){
                bookcaseDetailsBean.num=books.size
                bookcaseDetailsBean.books=books
                items.add(bookcaseDetailsBean)
            }
        }

        val rv_list=dialog.findViewById<MaxRecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = BookcaseDetailsAdapter(R.layout.item_bookcase_list, items)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceItemDeco(20))

        return this
    }


}