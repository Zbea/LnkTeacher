package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.SystemUpdateInfo
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.DP2PX


class AppSystemUpdateDialog(private val context: Context, private val item:SystemUpdateInfo){


    fun builder(): AppSystemUpdateDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_update_system)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 340f)) / 2
        dialog.show()

        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_update)
        btn_ok.setOnClickListener {
            AppUtils.startAPP(context,Constants.PACKAGE_SYSTEM_UPDATE)
        }
        val tv_name = dialog.findViewById<TextView>(R.id.tv_title)
        val tv_info = dialog.findViewById<TextView>(R.id.tv_info)
        tv_name?.text="系统更新："+item.version
        tv_info?.text=item.description

        return this
    }

}