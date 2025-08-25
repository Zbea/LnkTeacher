package com.bll.lnkteacher

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bll.lnkteacher.greendao.DaoMaster
import com.bll.lnkteacher.greendao.DaoSession
import com.bll.lnkteacher.greendao.GreenDaoUpgradeHelper
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.utils.SToast
import com.liulishuo.filedownloader.FileDownloader
import org.greenrobot.greendao.identityscope.IdentityScopeType
import kotlin.properties.Delegates


class MyApplication : Application(){


    companion object {

        private val TAG = "MyApplication"

        var mContext: Context by Delegates.notNull()
            private set
        var mDaoSession: DaoSession?=null
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        requestQueue = Volley.newRequestQueue(applicationContext)
        SPUtil.init(this)
        SToast.initToast(this)
        NetworkUtil.init(this)
        FileDownloader.setup(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        setDatabase()
    }

    /**
     * 配置greenDao
     */
    private fun setDatabase() {
        val mHelper = GreenDaoUpgradeHelper(this, "teaching.db" , null)
        val  db = mHelper.writableDatabase
        val mDaoMaster = DaoMaster(db)
        mDaoSession = mDaoMaster.newSession(IdentityScopeType.None)

    }

    private val mActivityLifecycleCallbacks = object :ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "onCreated: " + activity.componentName.className)
            ActivityManager.getInstance().addActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
            ActivityManager.getInstance().finishActivity(activity)

        }
    }


}
