package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupCreateDialog(val context: Context) {

    private var grade=0
    private var gradePopup:PopupRadioList?=null
    private var tv_grade:TextView?=null

    fun builder(): ClassGroupCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tv_grade = dialog.findViewById(R.id.tv_grade)

        tv_grade?.setOnClickListener {
            showPopGradeView()
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (name.isNotEmpty()&&grade>0)
            {
                dialog.dismiss()
                listener?.onClick(name,grade)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private fun showPopGradeView() {
        if (gradePopup==null){
            gradePopup= PopupRadioList(context, DataBeanManager.popupGrades, tv_grade!!,tv_grade?.width!!,  5).builder()
            gradePopup?.setOnSelectListener { item ->
                tv_grade?.text=item.name
                grade=item.id
            }
        }
        else{
            gradePopup?.show()
        }

    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,grade:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}