package com.bll.lnkteacher.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.DP2PX

class ProgressDialog(var context: Context, val screenPos: Int) {
    var mDialog: Dialog? = null

    init {
        createDialog()
    }

    private fun createDialog() {
        mDialog = Dialog(context)
        mDialog!!.setContentView(R.layout.dialog_progress)
        mDialog!!.setCanceledOnTouchOutside(false)
        val window = mDialog!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        if (screenPos == 2) {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        } else {
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        }
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,120f))/2
        window.attributes = layoutParams
    }

    fun setCanceledOutside(boolean: Boolean){
        mDialog?.setCanceledOnTouchOutside(boolean)
    }

    fun show() {
        val activity = context as Activity
        if (!activity.isFinishing && !activity.isDestroyed && mDialog != null && !mDialog!!.isShowing) {
            mDialog!!.show()
        }
    }

    fun dismiss() {
        val activity = context as Activity
        if (!activity.isFinishing && !activity.isDestroyed && mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
    }
}