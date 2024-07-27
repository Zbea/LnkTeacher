package com.bll.lnkteacher.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.*
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.manager.*
import com.bll.lnkteacher.mvp.model.AreaBean
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.classgroup.ClassGroupActivity
import com.bll.lnkteacher.ui.adapter.MainListAdapter
import com.bll.lnkteacher.ui.fragment.*
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SPUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_main.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class MainActivity : BaseActivity(),IContractView.IQiniuView {

    private val mQiniuPresenter=QiniuPresenter(this)
    private var mainLeftFragment: MainLeftFragment? = null
    private var mainRightFragment: MainRightFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var homeworkManagerFragment: HomeworkManagerFragment? = null
    private var noteFragment: NoteFragment? = null
    private var appFragment: AppFragment? = null
    private var textbookFragment: TextbookManagerFragment? = null
    private var examFragment: ExamManagerFragment? = null
    private var testpaperManagerFragment: TestPaperManagerFragment? = null
    private var learningConditionFragment: LearningConditionFragment? = null

    private var leftPosition = 0
    private var mAdapterLeft: MainListAdapter? = null
    private var leftFragment: Fragment? = null

    private var rightPosition = 0
    private var mAdapterRight: MainListAdapter? = null
    private var rightFragment: Fragment? = null

    private val myBroadcastReceiver=MyBroadcastReceiver()

    override fun onToken(token: String) {
        bookcaseFragment?.upload(token)
        textbookFragment?.upload(token)
        mainRightFragment?.uploadDiary(token)
        mainRightFragment?.uploadScreenShot(token)
    }

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        val areaJson = FileUtils.readFileContent(resources.assets.open("city.json"))
        val type= object : TypeToken<List<AreaBean>>() {}.type
        DataBeanManager.provinces = Gson().fromJson(areaJson, type)
    }

    override fun initView() {
        val intentFilter= IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(myBroadcastReceiver,intentFilter)

        val isTips=SPUtil.getBoolean("SpecificationTips")
        if (!isTips){
            showView(ll_tips)
        }

        mainLeftFragment=MainLeftFragment()
        mainRightFragment = MainRightFragment()
        textbookFragment= TextbookManagerFragment()
        bookcaseFragment = BookCaseFragment()
        homeworkManagerFragment = HomeworkManagerFragment()
        noteFragment= NoteFragment()
        appFragment = AppFragment()
        examFragment= ExamManagerFragment()
        testpaperManagerFragment= TestPaperManagerFragment()
        learningConditionFragment= LearningConditionFragment()

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
                    4 -> switchFragment(1,appFragment)//应用
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
            customStartActivity(Intent(this,AccountInfoActivity::class.java))
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
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            var selectLong = timeInMillis
            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }
            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_REFRESH
            val pendingIntent =if (Build.VERSION.SDK_INT >= 31)
                PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            else
                PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
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

    /**
     * 清除本地所有数据
     */
    private fun clearSqlData(){
        SPUtil.removeObj("privacyPasswordDiary")
        SPUtil.removeObj("privacyPasswordNote")

        MyApplication.mDaoSession?.clear()
        AppDaoManager.getInstance().clear()
        BookGreenDaoManager.getInstance().clear()
        ClassScheduleGreenDaoManager.getInstance().clear()
        DiaryDaoManager.getInstance().clear()
        FreeNoteDaoManager.getInstance().clear()
        ItemTypeDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()
        NoteDaoManager.getInstance().clear()
        WallpaperDaoManager.getInstance().clear()
        DateEventDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.BOOK_PATH))
        FileUtils.deleteFile(File(Constants.SCREEN_PATH))
        FileUtils.deleteFile(File(Constants.ZIP_PATH).parentFile)

        EventBus.getDefault().post(Constants.BOOK_EVENT)
        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
        EventBus.getDefault().post(Constants.NOTE_BOOK_MANAGER_EVENT)
        EventBus.getDefault().post(Constants.NOTE_EVENT)
        EventBus.getDefault().post(Constants.COURSE_EVENT)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            //上传
            Constants.DATA_UPLOAD_EVENT->{
                mQiniuPresenter.getToken()
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
        if (myBroadcastReceiver!=null){
            unregisterReceiver(myBroadcastReceiver)
        }
    }

}