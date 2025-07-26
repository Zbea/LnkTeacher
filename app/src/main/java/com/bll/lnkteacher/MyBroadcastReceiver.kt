package com.bll.lnkteacher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
import org.greenrobot.eventbus.EventBus
import java.io.File

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
            "android.intent.action.PACKAGE_ADDED"->{
                Log.d(Constants.DEBUG,"应用安装")
                EventBus.getDefault().post(Constants.APP_INSTALL_EVENT)
                if (intent.data?.schemeSpecificPart.equals(context.packageName)) {
                    Log.d(Constants.DEBUG,"更新launcher")
                    //安装完成后删除
                    FileUtils.deleteFile(File(FileAddress().getLauncherPath()))
                    // 应用安装完成后重启
                    AppUtils.reOpenApk(context)
                }
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
            //监听网络变化
            ConnectivityManager.CONNECTIVITY_ACTION->{
                val info: NetworkInfo? = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (info!!.state.equals(NetworkInfo.State.CONNECTED)) {
                    val isNet = NetworkInfo.State.CONNECTED == info.state && info.isAvailable
                    Log.d("debug", "监听网络变化$isNet")
                    if (isNet)
                        EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT)
                }
            }
            Constants.NET_REFRESH->{
                if (NetworkUtil.isNetworkConnected()){
                    EventBus.getDefault().post(Constants.NETWORK_CONNECTION_COMPLETE_EVENT )
                }
            }
            DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED->{
                val displayManager=context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val wifiDisplayStatus=displayManager.getWifiDisplayStatus()
                if (wifiDisplayStatus!=null&&wifiDisplayStatus.getActiveDisplay()!=null){
                    DataBeanManager.isConnectDisplayStatus=true
                    Log.d("debug", "监听投屏连接")
                }
                else{
                    DataBeanManager.isConnectDisplayStatus=false
                    Log.d("debug", "监听投屏断开")
                }
            }
        }
    }
}