package com.bll.lnkteacher.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.mvp.model.book.BookcaseDetailsBean
import com.bll.lnkteacher.widget.FlowLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookcaseDetailsAdapter(layoutResId: Int, data: List<BookcaseDetailsBean>?) : BaseQuickAdapter<BookcaseDetailsBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BookcaseDetailsBean) {
        helper.setText(R.id.tv_book_type,item.typeStr)
        helper.setText(R.id.tv_book_num,"共计："+item.num+"本")

        val recyclerView = helper.getView<RecyclerView>(R.id.rv_list)
        recyclerView?.layoutManager = FlowLayoutManager()
        val mAdapter = ChildAdapter(R.layout.item_bookcase_name,item.books)
        recyclerView?.adapter = mAdapter
    }

    class ChildAdapter(layoutResId: Int,  data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: Book) {
            helper.apply {
                helper.setText(R.id.tv_name, item.bookName)
            }
        }
    }

}
