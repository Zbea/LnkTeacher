package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupCreateDialog(val context: Context) {


    fun builder(): ClassGroupCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (!name.isNullOrEmpty())
            {
                dialog.dismiss()
                listener?.onClick(name)
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

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}