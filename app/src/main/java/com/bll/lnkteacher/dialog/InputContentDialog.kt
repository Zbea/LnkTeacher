package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.KeyboardUtils

class InputContentDialog(val context: Context, private val screenPos:Int,val string: String,val type: Int) {

    constructor(context: Context ,screenPos: Int,string: String) :this(context,screenPos,string,0)
    constructor(context: Context ,string: String) :this(context,1,string,0)

    private var tvName:EditText?=null

    fun builder(): InputContentDialog{
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_input_content)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        if (screenPos==1){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,460f))/2
        }
        else{
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,460f))/2
        }
        dialog.show()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        tvName = dialog.findViewById(R.id.ed_name)
        tvName?.hint=string
        if (type==1)
            tvName?.inputType = InputType.TYPE_CLASS_NUMBER // 设置输入类型为数字

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val content = tvName?.text.toString()
            if (content.isNotEmpty()) {
                dialog.dismiss()
                listener?.onClick(content)
            }
        }
        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(string: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}