package com.bll.lnkteacher.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.bll.lnkteacher.R

class ClassGroupPermissionTipsDialog(val context: Context,val view: View) {

    private var mPopupWindow: PopupWindow?=null
    private var width=0
    private var height=0

    fun builder(): ClassGroupPermissionTipsDialog{
        val popView = LayoutInflater.from(context).inflate(R.layout.dialog_classgroup_permission_tips, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView=popView
            isFocusable=true // 设置PopupWindow可获得焦点
            isTouchable=true // 设置PopupWindow可触摸
            isOutsideTouchable=true // 设置非PopupWindow区域可触摸
            isClippingEnabled = false
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        width=mPopupWindow?.contentView?.measuredWidth!!
        height=mPopupWindow?.contentView?.measuredHeight!!

        show()
        return this
    }

    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view,-(width-230)/2, -height-60, Gravity.START)
        }
    }
}