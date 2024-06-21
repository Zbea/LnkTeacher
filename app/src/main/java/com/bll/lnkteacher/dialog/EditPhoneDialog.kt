package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.ToolUtils

class EditPhoneDialog(val context: Context) {
    fun builder(): EditPhoneDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_account_edit_phone)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val ed_phone = dialog.findViewById<EditText>(R.id.ed_phone)
        val ed_code = dialog.findViewById<EditText>(R.id.ed_code)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val phone=ed_phone.text.toString()
            val code=ed_code.text.toString()
            if (phone.isNotEmpty()&&code.isNotEmpty())
            {
                if (ToolUtils.isPhoneNum(phone)){
                    dialog.dismiss()
                    listener?.onClick(code, phone)
                }
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(code: String,phone:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}