package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils


class NotebookAddDialog(private val context: Context, val title: String, val name:String, private val nameHint:String) {


    fun builder(): NotebookAddDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_notebook_add)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        val tvTitle=dialog.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text = title

        val etName=dialog.findViewById<EditText>(R.id.et_name)
        etName?.setText(name)
        etName?.hint=nameHint

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val nameStr=etName?.text.toString()
            if (nameStr.isNotEmpty())
            {
                listener?.onClick(nameStr)
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
        fun onClick(string: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}