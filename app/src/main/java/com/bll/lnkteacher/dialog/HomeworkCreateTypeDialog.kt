package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

/**
 * 教学 作业本创建
 */
class HomeworkCreateTypeDialog(val context: Context, val hint: String) {


    fun builder(): HomeworkCreateTypeDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_type_create)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val etName = dialog.findViewById<EditText>(R.id.et_name)
        etName.hint = hint

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            var content = etName.text.toString()
            if (!content.isNullOrEmpty()) {
                dialog.dismiss()
                listener?.onClick(content)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}