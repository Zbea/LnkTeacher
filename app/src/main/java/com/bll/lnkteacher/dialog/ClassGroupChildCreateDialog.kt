package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupChildCreateDialog(val context: Context, val titleStr:String) {

    fun builder(): ClassGroupChildCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_child_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        if (titleStr.isNotEmpty()){
            et_name.setText(titleStr)
            et_name.setSelection(titleStr.length)
            tv_title.text="修改子群"
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty())
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