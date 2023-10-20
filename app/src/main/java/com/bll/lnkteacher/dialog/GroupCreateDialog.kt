package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class GroupCreateDialog(val context: Context) {

    private var classIds= mutableListOf<Int>()
    private var grade=0

    fun builder(): GroupCreateDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_group_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val tv_class_name = dialog.findViewById<TextView>(R.id.tv_class_name)
        val tv_grade = dialog.findViewById<TextView>(R.id.tv_grade)

        tv_class_name.setOnClickListener {
            val pops=if (grade==0) DataBeanManager.popClassGroups else DataBeanManager.getGradeClassGroups(grade)
            PopupCheckList(context, pops, tv_class_name,tv_class_name.width,  5).builder()
                ?.setOnSelectListener { items ->
                for (item in items) {
                    if (!classIds.contains(item.id))
                        classIds.add(item.id)
                }
            }
        }

        val popGrades=DataBeanManager.popupGrades
        tv_grade.setOnClickListener {
            PopupRadioList(context, popGrades, tv_grade, tv_grade.width,5).builder()
                .setOnSelectListener { item ->
                    grade = item.id
                    tv_grade.text = item.name
                }
        }

        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty()&&grade>0)
            {
                dialog.dismiss()
                listener?.onClick(name,grade,classIds.toIntArray())
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }



    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,grade:Int,classIds:IntArray)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }



}