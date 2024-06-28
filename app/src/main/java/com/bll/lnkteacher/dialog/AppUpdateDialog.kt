package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AppUpdateBean
import com.bll.lnkteacher.utils.DP2PX


class AppUpdateDialog(private val context: Context,private val item:AppUpdateBean){

    private var dialog:Dialog?=null
    private var btn_ok:TextView?=null

    fun builder(): AppUpdateDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_update)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setCanceledOnTouchOutside(false)
        val window = dialog?.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 340f)) / 2
        dialog?.show()

        btn_ok = dialog?.findViewById(R.id.tv_update)
        val tv_name = dialog?.findViewById<TextView>(R.id.tv_title)
        val tv_info = dialog?.findViewById<TextView>(R.id.tv_info)
        tv_name?.text="应用更新："+item.versionName
        tv_info?.text=item.versionInfo

        return this
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun setUpdateBtn(string: String){
        if (btn_ok!=null){
            btn_ok?.text = string
        }
    }
}