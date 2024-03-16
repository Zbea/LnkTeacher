package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.SToast


class PrivacyPasswordFindDialog(private val context: Context) {

    fun builder(): PrivacyPasswordFindDialog? {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_privacy_password_find)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val checkPassword=MethodManager.getPrivacyPassword()

        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val etPasswordAgain=dialog.findViewById<EditText>(R.id.et_password_again)
        val etPasswordFind=dialog.findViewById<EditText>(R.id.et_question_password)
        val tvFind=dialog.findViewById<TextView>(R.id.tv_question_password)
        tvFind.text=checkPassword?.question


        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val passwordAgainStr=etPasswordAgain?.text.toString()
            val passwordFindStr=etPasswordFind?.text.toString()

            if (passwordFindStr.isEmpty()){
                SToast.showText(1,"请输入密保")
                return@setOnClickListener
            }

            if (passwordFindStr!=checkPassword?.answer){
                SToast.showText(1,"密保错误")
                return@setOnClickListener
            }
            if (passwordStr.isEmpty()||passwordAgainStr.isEmpty()){
                SToast.showText(1,"请输入密码")
                return@setOnClickListener
            }
            if (passwordStr!=passwordAgainStr){
                SToast.showText(1,"密码输入不一致")
                return@setOnClickListener
            }

            checkPassword.password= MD5Utils.digest(passwordStr)
            MethodManager.savePrivacyPassword(checkPassword)
            dialog.dismiss()
            listener?.onClick()

        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}