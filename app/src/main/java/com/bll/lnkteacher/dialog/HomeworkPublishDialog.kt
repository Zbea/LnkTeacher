package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.SToast

class HomeworkPublishDialog(val context: Context) {

    private var selectDialog: HomeworkPublishClassGroupSelectDialog? = null
    private var selectClasss= mutableListOf<HomeworkClass>()
    fun builder(): HomeworkPublishDialog {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_homework_publish)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val tv_send = dialog.findViewById<TextView>(R.id.tv_send)
        val tv_class_name = dialog.findViewById<TextView>(R.id.tv_class_name)
        val etContent = dialog.findViewById<EditText>(R.id.et_content)

        tv_class_name.setOnClickListener {
            getSelectClass()
        }

        tv_send.setOnClickListener {
            val contentStr = etContent.text.toString()
            if (contentStr.isNotEmpty()) {
                if (selectClasss.isNotEmpty())
                {
                    dialog.dismiss()
                    listener?.onSend(contentStr,selectClasss)
                }
                else{
                    SToast.showText("未选中发送班级")
                }
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    /**
     * 班级选择
     */
    private fun getSelectClass() {
        if (selectDialog == null) {
            selectDialog = HomeworkPublishClassGroupSelectDialog(context).builder()
            selectDialog?.setOnDialogClickListener {
                selectClasss= it
            }
        } else {
            selectDialog?.show()
        }

    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSend(contentStr:String,classs:List<HomeworkClass>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}