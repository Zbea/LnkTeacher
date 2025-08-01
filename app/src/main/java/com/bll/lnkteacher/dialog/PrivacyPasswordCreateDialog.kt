package com.bll.lnkteacher.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.ToolUtils


class PrivacyPasswordCreateDialog(private val context: Context) {

    fun builder(): PrivacyPasswordCreateDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_privacy_password_create)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val ed_phone = dialog.findViewById<TextView>(R.id.et_phone)
        ed_phone.text = if (MethodManager.getUser().telNumber.isNullOrEmpty()) "" else MethodManager.getUser().telNumber
        val ed_code = dialog.findViewById<EditText>(R.id.et_code)
        val btn_code = dialog.findViewById<TextView>(R.id.btn_code)
        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString()
            if (ToolUtils.isPhoneNum(phone)){
                listener?.onPhone(phone)
                btn_code.isEnabled = false
                btn_code.isClickable = false
                object : CountDownTimer(60 * 1000, 1000) {
                    override fun onFinish() {
                        btn_code.isEnabled = true
                        btn_code.isClickable = true
                        btn_code.text = "获取验证码"
                    }
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        btn_code.text = "${millisUntilFinished / 1000}s"
                    }
                }.start()
            }
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            val code=ed_code?.text.toString()
            if (passwordStr.isNotEmpty()&&code.isNotEmpty()){
                val checkPassword= PrivacyPassword()
                checkPassword.password= MD5Utils.digest(passwordStr)
                checkPassword.isSet=true
                dialog.dismiss()
                listener?.onSave(checkPassword,code)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }


    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onSave(privacyPassword: PrivacyPassword,code:String)
        fun onPhone(phone:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}