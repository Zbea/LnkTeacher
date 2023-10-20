package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class GroupAddDialog(val context: Context) {

    private var classIds= mutableListOf<Int>()

    fun builder(): GroupAddDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_group_add)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val tv_class_name = dialog.findViewById<TextView>(R.id.tv_class_name)

        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty())
            {
                dialog.dismiss()
                listener?.onClick(name,classIds.toIntArray())
            }
        }
        val pops= DataBeanManager.popClassGroups
        tv_class_name.setOnClickListener {
            PopupCheckList(context, pops, tv_class_name,tv_class_name.width,  5).builder()
                ?.setOnSelectListener { items ->
                    for (item in items) {
                        if (!classIds.contains(item.id))
                            classIds.add(item.id)
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
        fun onClick(num: String,classIds:IntArray)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }



}