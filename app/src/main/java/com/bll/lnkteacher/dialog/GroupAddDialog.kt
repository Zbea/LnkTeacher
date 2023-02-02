package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.PopWindowBean
import com.bll.lnkteacher.utils.KeyboardUtils

class GroupAddDialog(val context: Context, val type:Int) {

    private var classIds= mutableListOf<Int>()

    fun builder(): GroupAddDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_group_add)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.text=(if (type==2) "加入校群" else "加入际群")
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        et_name.hint=if (type==2) "校群名称" else "际群名称"
        val tv_class_name = dialog.findViewById<TextView>(R.id.tv_class_name)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (!name.isNullOrEmpty())
            {
                dialog.dismiss()
                listener?.onClick(name,classIds.toIntArray())
            }
        }
        tv_class_name.setOnClickListener {
            selectorClassGroup(tv_class_name)
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var popWindow:PopWindowCheckList?=null
    private fun selectorClassGroup(view: View){
        val groups= DataBeanManager.getInstance().classGroups
        val pops= mutableListOf<PopWindowBean>()
        for (item in groups){
            pops.add(PopWindowBean(item.classId,item.name,false))
        }
        if (popWindow==null)
        {
            popWindow= PopWindowCheckList(context, pops, view , 5).builder()
            popWindow  ?.setOnSelectListener(object : PopWindowCheckList.OnSelectListener {
                override fun onSelect(items: List<PopWindowBean>) {
                    for (item in items){
                        classIds.add(item.id)
                    }
                }
            })
        }
        else{
            popWindow?.show()
        }
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,classIds:IntArray)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }



}