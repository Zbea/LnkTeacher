package com.bll.lnkteacher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.greenrobot.eventbus.EventBus

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            "android.intent.action.PACKAGE_ADDED"->{
                EventBus.getDefault().post(Constants.APP_INSTALL_EVENT)
            }
            "android.intent.action.PACKAGE_REMOVED"->{
                EventBus.getDefault().post(Constants.APP_UNINSTALL_EVENT)
            }
            Constants.DATA_UPLOAD_BROADCAST_EVENT->{
                Log.d("debug","上传")
                EventBus.getDefault().postSticky(Constants.DATA_UPLOAD_EVENT)
            }
            Constants.ACTION_REFRESH->{
                Log.d("debug","每天0点刷新页面")
                EventBus.getDefault().postSticky(Constants.AUTO_REFRESH_EVENT)
            }
        }
    }
}