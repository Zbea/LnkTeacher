package com.bll.lnkteacher.ui.adapter

import android.widget.ImageView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class BookAdapter(layoutResId: Int, data: List<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Book) {
        helper.setText(R.id.tv_name, item.bookName)
        val image = helper.getView<ImageView>(R.id.iv_image)
        GlideUtils.setImageRoundUrl(mContext, item.imageUrl, image, 8)
    }

}
