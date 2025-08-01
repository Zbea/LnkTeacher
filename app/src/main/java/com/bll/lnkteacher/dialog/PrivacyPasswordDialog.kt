package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
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
import com.bll.lnkteacher.utils.SToast


class PrivacyPasswordDialog(private val context: Context,private val type:Int=0) {

    private var privacyPassword:PrivacyPassword?=null

    fun builder(): PrivacyPasswordDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_privacy_password)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,500f))/2
        dialog.show()

        getPrivacyPassword()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val etPassword=dialog.findViewById<EditText>(R.id.et_password)
        val tvFind = dialog.findViewById<TextView>(R.id.tv_find_password)

        tvFind.setOnClickListener {
            PrivacyPasswordCreateDialog(context).builder().setOnDialogClickListener(object : PrivacyPasswordCreateDialog.OnDialogClickListener {
                override fun onSave(privacyPassword: PrivacyPassword, code: String) {
                    listener?.onSave(privacyPassword,code)
                }
                override fun onPhone(phone: String) {
                    listener?.onPhone(phone)
                }
            })
        }

        val tvEdit = dialog.findViewById<TextView>(R.id.tv_edit_password)
        tvEdit.setOnClickListener {
            PrivacyPasswordEditDialog(context,type).builder().setOnDialogClickListener{
                getPrivacyPassword()
            }
        }

        btn_cancel?.setOnClickListener { dialog.dismiss() }
        btn_ok?.setOnClickListener {
            val passwordStr=etPassword?.text.toString()
            if (passwordStr.isNotEmpty()){
                if (MD5Utils.digest(passwordStr) != privacyPassword?.password){
                    SToast.showText(2,"密码错误")
                    etPassword.setText("")
                    return@setOnClickListener
                }
                listener?.onClick()
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        return this
    }

    /**
     * 刷新当前密码
     */
    fun getPrivacyPassword(){
        privacyPassword=MethodManager.getPrivacyPassword(type)
    }

    private var listener: OnDialogClickListener? = null

    interface OnDialogClickListener {
        fun onClick()
        fun onSave(privacyPassword: PrivacyPassword, code:String)
        fun onPhone(phone:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

}