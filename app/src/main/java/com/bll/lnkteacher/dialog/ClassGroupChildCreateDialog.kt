package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.SToast

class ClassGroupChildCreateDialog(val context: Context, val titleStr:String, private val users:MutableList<ClassGroupUser>) {
    var ids= mutableListOf<Int>()

    fun builder(): ClassGroupChildCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_child_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_select = dialog.findViewById<TextView>(R.id.tv_select)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        if (titleStr.isNotEmpty()){
            et_name.setText(titleStr)
            et_name.setSelection(titleStr.length)
        }

        if (users.isEmpty()){
            tv_title.text="修改子群"
            tv_select.visibility= View.GONE
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        tv_select.setOnClickListener {
            ClassGroupUserSelectorDialog(context,users).builder().setOnDialogClickListener{
                ids= it as MutableList<Int>
            }
        }

        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty())
            {
                if (users.isNotEmpty()){
                    if (ids.size==0){
                        SToast.showText(2,"请添加学生")
                        return@setOnClickListener
                    }
                }
                dialog.dismiss()
                listener?.onClick(name, ids)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,ids:MutableList<Int>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}