package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.utils.SToast


/**
 * book收藏、删除、移动
 */
class BookManageDialog(val context: Context, val type:Int, val book:Book){

    fun builder(): BookManageDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_book_manage)
        dialog.show()
        val window = dialog.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)

        val tv_name=dialog.findViewById<TextView>(R.id.tv_name)
        val iv_close=dialog.findViewById<ImageView>(R.id.iv_close)
        val ll_content=dialog.findViewById<LinearLayout>(R.id.ll_content)
        val ll_content1=dialog.findViewById<LinearLayout>(R.id.ll_content1)
        val ll_collect=dialog.findViewById<LinearLayout>(R.id.ll_collect)
        val ll_delete=dialog.findViewById<LinearLayout>(R.id.ll_delete)
        val ll_move=dialog.findViewById<LinearLayout>(R.id.ll_move)

        ll_content.visibility= if (type==0) View.GONE else View.VISIBLE
        ll_content1.visibility= if (type==0) View.VISIBLE else View.GONE

        tv_name.text=book.name

        iv_close.setOnClickListener {
            dialog.dismiss()
        }

        ll_collect.setOnClickListener {
            if (book.isCollect){
                SToast.showText("已收藏")
                return@setOnClickListener
            }
            if (onClickListener!=null)
                onClickListener?.onCollect()
            dialog.dismiss()
        }

        ll_delete.setOnClickListener {
            if (onClickListener!=null)
                onClickListener?.onDelete()
            dialog.dismiss()
        }

        ll_move.setOnClickListener {
            if (onClickListener!=null)
                onClickListener?.onMove()
            dialog.dismiss()
        }

        return this
    }



    private var onClickListener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onCollect()
        fun onDelete()
        fun onMove()
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }



}