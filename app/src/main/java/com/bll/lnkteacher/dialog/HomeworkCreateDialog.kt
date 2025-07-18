package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.SToast

class HomeworkCreateDialog(val context: Context, val grade:Int,val type: Int) {

    private var classGroupIds= mutableListOf<Int>()

    fun builder(): HomeworkCreateDialog{
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_create)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,450f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.tv_ok)
        val tvClass = dialog.findViewById<TextView>(R.id.tv_class)
        val tvName = dialog.findViewById<EditText>(R.id.ed_name)

        var hintStr=""
        var typeName=""

        when(type){
            1->{
                hintStr="输入作业本名称"
                typeName="作业本"
            }
            2->{
                hintStr="输入作业卷名称"
                typeName="作业卷"
            }
            3->{
                hintStr="输入手写本名称"
                typeName="手写本"
            }
        }

        tvName?.hint=hintStr
        tvName?.setText(typeName)
        tvName?.setSelection(0)

        tvClass.setOnClickListener {
            ClassGroupSelectorDialog(context,2, DataBeanManager.getClassGroups(grade)).builder().setOnDialogSelectListener{
                classGroupIds= it.toMutableList()
            }
        }

        btn_ok.setOnClickListener {
            val content = tvName?.text.toString()
            if (content.isNotEmpty()) {
                dialog.dismiss()
                listener?.onClick(content,classGroupIds)
            }
            else{
                SToast.showText(2,"请输入名称")
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(string: String,ids:List<Int>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}