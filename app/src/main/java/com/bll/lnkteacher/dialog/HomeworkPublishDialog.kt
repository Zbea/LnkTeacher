package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.HomeworkAssign
import com.bll.lnkteacher.utils.KeyboardUtils

class HomeworkPublishDialog(val context: Context) {


    fun builder(): HomeworkPublishDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_publish)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val tv_send = dialog.findViewById<TextView>(R.id.tv_send)
        val tv_class_name = dialog.findViewById<TextView>(R.id.tv_class_name)
        val etContent = dialog.findViewById<EditText>(R.id.et_content)

        tv_class_name.setOnClickListener {
            HomeworkPublishClassgroupSelectorDialog(context).builder()
                ?.setOnDialogClickListener {

                }
        }

        tv_send.setOnClickListener {
            val contentStr=etContent.text.toString()
            if (!contentStr.isNullOrEmpty())
            {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(item: HomeworkAssign)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}