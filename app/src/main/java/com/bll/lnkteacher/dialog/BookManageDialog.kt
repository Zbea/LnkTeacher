package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Book


/**
 * book收藏、删除、移动
 */
class BookManageDialog(val context: Context, val book:Book){

    fun builder(): BookManageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_book_manage)
        dialog.show()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        val tv_name=dialog.findViewById<TextView>(R.id.tv_name)
        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        val ll_delete=dialog.findViewById<LinearLayout>(R.id.ll_delete)

        tv_name.text=book.bookName+if (book.semester.isNullOrEmpty()) "" else "-"+book.semester

        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        ll_delete.setOnClickListener {
            if (onClickListener!=null)
                onClickListener?.onDelete()
            dialog.dismiss()
        }

        return this
    }

    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onDelete()
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}