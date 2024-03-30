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
            Constants.ACTION_UPLOAD_REFRESH->{
                Log.d("debug","每天三点刷新、上传")
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_DAY_EVENT)
            }
            Constants.ACTION_UPLOAD_YEAR->{
                Log.d("debug","每年12月31日更新上传")
                EventBus.getDefault().postSticky(Constants.AUTO_UPLOAD_YEAR_EVENT)
            }
            Constants.DATA_CLEAR_BROADCAST_EVENT->{
                Log.d("debug","一键导入")
                EventBus.getDefault().postSticky(Constants.SETTING_DATA_CLEAR_EVENT)
            }
            Constants.ACTION_REFRESH->{
                Log.d("debug","每天0点刷新页面")
                EventBus.getDefault().postSticky(Constants.AUTO_REFRESH_EVENT)
            }
        }
    }
}