package com.bll.lnkteacher.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiv.aisdk.NativeLib
import com.bll.lnkteacher.*
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.manager.*
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.activity.account.AccountInfoActivity
import com.bll.lnkteacher.ui.activity.classgroup.ClassGroupActivity
import com.bll.lnkteacher.ui.adapter.MainListAdapter
import com.bll.lnkteacher.ui.fragment.*
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_main.*
import java.io.File
import java.util.*

class MainActivity : BaseAppCompatActivity() {

    private var mainLeftFragment: MainLeftFragment? = null
    private var mainRightFragment: MainRightFragment? = null
    private var bookcaseFragment: BookcaseFragment? = null
    private var homeworkManagerFragment: HomeworkManagerFragment? = null
    private var noteFragment: NoteFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var examFragment: ExamManagerFragment? = null
    private var testpaperManagerFragment: TestPaperManagerFragment? = null
    private var learningConditionFragment: LearningConditionFragment? = null
    private var documentManagerFragment: DocumentManagerFragment?=null

    private var leftPosition = 0
    private var mAdapterLeft: MainListAdapter? = null
    private var leftFragment: Fragment? = null

    private var rightPosition = 0
    private var mAdapterRight: MainListAdapter? = null
    private var rightFragment: Fragment? = null

    private val myBroadcastReceiver=MyBroadcastReceiver()
    private var mqttClient:MQTTClient?=null

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        mqttClient=MQTTClient().getInstance()
        mqttClient?.init(this)
        mqttClient?.connect()

        //创建截图默认分类
        val screenshotPath=FileAddress().getPathScreen("未分类")
        if (!FileUtils.isExist(screenshotPath)){
            FileUtils.mkdirs(screenshotPath)
        }

        val path = FileAddress().getPathDocument("默认")
        if (!FileUtils.isExist(path)){
            MethodManager.createFileScan(this,path)
        }

        //删除launcherApk
        val targetFileStr = FileAddress().getLauncherPath()
        if (FileUtils.isExist(targetFileStr)){
            FileUtils.deleteFile(File(targetFileStr))
        }

        //创建书架分类
        if (ItemTypeDaoManager.getInstance().queryAll(2).size==0){
            val strings = DataBeanManager.bookType
            for (i in strings.indices) {
                val item = ItemTypeBean()
                item.type=2
                item.title = strings[i]
                item.date=System.currentTimeMillis()
                ItemTypeDaoManager.getInstance().insertOrReplace(item)
            }
        }
    }

    override fun initView() {
        val intentFilter= IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED)
        registerReceiver(myBroadcastReceiver,intentFilter)

        val isTips=SPUtil.getBoolean("SpecificationTips")
        if (!isTips){
            showView(ll_tips)
        }

        mainLeftFragment=MainLeftFragment()
        mainRightFragment = MainRightFragment()
        textbookFragment= TextbookFragment()
        bookcaseFragment = BookcaseFragment()
        homeworkManagerFragment = HomeworkManagerFragment()
        noteFragment= NoteFragment()
        examFragment= ExamManagerFragment()
        testpaperManagerFragment= TestPaperManagerFragment()
        learningConditionFragment= LearningConditionFragment()
        documentManagerFragment= DocumentManagerFragment()

        switchFragment(1, mainLeftFragment)
        switchFragment(2, mainRightFragment)

        mAdapterLeft = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexLeftData()).apply {
            rv_list_a.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_a.adapter = this
            bindToRecyclerView(rv_list_a)
            setOnItemClickListener { adapter, view, position ->
                updateItem(leftPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(1,mainLeftFragment)//首页
                    1 -> switchFragment(1,bookcaseFragment)//书架
                    2 -> switchFragment(1,textbookFragment)//课本
                    3 -> switchFragment(1,learningConditionFragment)//义教
                    4 -> switchFragment(1,documentManagerFragment)//应用
                }
                leftPosition = position
            }
        }

        mAdapterRight = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexRightData()).apply {
            rv_list_b.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list_b.adapter = this
            bindToRecyclerView(rv_list_b)
            setOnItemClickListener { adapter, view, position ->
                updateItem(rightPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(2,  mainRightFragment)
                    1 -> switchFragment(2,  homeworkManagerFragment)
                    2 -> switchFragment(2,  testpaperManagerFragment)
                    3 -> switchFragment(2,  examFragment)
                    4 -> switchFragment(2,  noteFragment)
                }
                rightPosition = position
            }
        }

        iv_user_a.setOnClickListener {
            customStartActivity(Intent(this, AccountInfoActivity::class.java))
        }

        iv_classgroup.setOnClickListener {
            customStartActivity(Intent(this, ClassGroupActivity::class.java))
        }

        ll_tips.setOnClickListener {
            disMissView(ll_tips)
            SPUtil.putBoolean("SpecificationTips",true)
        }

        startRemind()
    }


    /**
     * 开始每天定时任务
     */
    private fun startRemind() {
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            var selectLong = timeInMillis
            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }
            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_REFRESH
            val pendingIntent=PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.SYS_RTC_WAKEUP, selectLong,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }
    }

    private fun switchFragment(type: Int, to: Fragment?) {
        val from = if (type == 1) {
            leftFragment
        } else {
            rightFragment
        }
        if (from != to) {
            if (type == 1) {
                leftFragment = to
            } else {
                rightFragment = to
            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(if (type == 1) R.id.frame_layout_a else R.id.frame_layout_b, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_APANEL_BACK || keyCode == KeyEvent.KEYCODE_BPANEL_BACK) {
            false
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClient?.disconnect()
        unregisterReceiver(myBroadcastReceiver)
    }

    override fun onRefreshData() {
        Handler().postDelayed({
            if (mqttClient?.isConnect() == false){
                mqttClient?.connect()
            }
        },20*1000)
    }

}