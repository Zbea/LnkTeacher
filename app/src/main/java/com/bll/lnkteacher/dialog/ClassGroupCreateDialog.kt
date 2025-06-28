package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupCreateDialog(val context: Context,val type:Int,private val name:String,private var grade: Int) {

    constructor(context: Context,type: Int):this(context, type,"",0)

    fun builder(): ClassGroupCreateDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tv_grade = dialog.findViewById<TextView>(R.id.tv_grade)
        if (grade!=0){
            tv_grade.text=DataBeanManager.getGradeStr(grade)
        }
        if (type==1){
            tv_title.text="创建班群"
        }
        else{
            tv_title.text="创建辅群"
        }

        if (name.isNotEmpty()){
            et_name.setText(name)
            et_name.setSelection(name.length)
            if (type==1){
                tv_title.text="修改班群"
            }
            else{
                tv_title.text="修改辅群"
            }
        }

        val grades=DataBeanManager.popupGrades(grade)

        tv_grade?.setOnClickListener {
            PopupRadioList(context, grades, tv_grade,  5).builder().setOnSelectListener { item ->
                tv_grade.text=item.name
                grade=item.id
            }
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

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String,grade:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }

}