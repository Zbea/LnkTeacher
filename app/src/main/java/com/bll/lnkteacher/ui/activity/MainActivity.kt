package com.bll.lnkteacher.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.bll.lnkteacher.ui.adapter.MainListAdapter
import com.bll.lnkteacher.ui.fragment.*
import com.bll.lnkteacher.utils.DateUtils
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
    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var lastFragment: Fragment? = null
    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var classGroupFragment: ClassGroupFragment? = null
    private var homeworkManagerFragment: HomeworkManagerFragment? = null
    private var noteFragment: NoteFragment? = null
    private var appFragment: AppFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var examFragment: ExamManagerFragment? = null
    private var typeEvent=""

    override fun onToken(token: String) {
        when(typeEvent){
            //每天更新
            Constants.AUTO_UPLOAD_DAY_EVENT->{
                bookcaseFragment?.upload(token)
                textbookFragment?.upload(token)
            }
            //每年更新
            Constants.AUTO_UPLOAD_YEAR_EVENT->{
                noteFragment?.upload(token)
                mainFragment?.uploadDiary(token)
                mainFragment?.uploadFreeNote(token)
                mainFragment?.uploadScreenShot(token)
            }
        }
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

        mainFragment = MainFragment()
        textbookFragment= TextbookFragment()
        bookcaseFragment = BookCaseFragment()
        classGroupFragment= ClassGroupFragment()
        homeworkManagerFragment = HomeworkManagerFragment()
        noteFragment= NoteFragment()
        appFragment = AppFragment()
        examFragment= ExamManagerFragment()

        switchFragment(lastFragment, mainFragment)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mHomeAdapter = MainListAdapter(R.layout.item_main_list, DataBeanManager.getIndexData())
        rv_list.adapter = mHomeAdapter
        mHomeAdapter?.bindToRecyclerView(rv_list)
        mHomeAdapter?.setOnItemClickListener { adapter, view, position ->

            mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
            mHomeAdapter?.updateItem(position, true)//更新新的位置

            when (position) {
                0 -> switchFragment(lastFragment, mainFragment)
                1 -> switchFragment(lastFragment, bookcaseFragment)
                2 -> switchFragment(lastFragment, textbookFragment)
                3 -> switchFragment(lastFragment, classGroupFragment)
                4 -> switchFragment(lastFragment, homeworkManagerFragment)
                5 -> switchFragment(lastFragment, examFragment)
                6 -> switchFragment(lastFragment, noteFragment)
                7 -> switchFragment(lastFragment, appFragment)
            }

            lastPosition=position

        }

        iv_user.setOnClickListener {
            startActivity(Intent(this,AccountInfoActivity::class.java))
        }

        startRemind()
        startRemindDayUpload()
        startRemind12Month()
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

    /**
     * 开始每天定时自动上传
     */
    private fun startRemindDayUpload() {
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            var selectLong = timeInMillis
            if (currentTimeMillisLong > selectLong) {
                add(Calendar.DAY_OF_MONTH, 1)
                selectLong = timeInMillis
            }
            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_REFRESH
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


    /**
     * 每年12月31 3点执行
     */
    private fun startRemind12Month() {
        val allDay=if (DateUtils().isYear(DateUtils.getYear())) 366 else 365
        val date=allDay*24*60*60*1000L
        Calendar.getInstance().apply {
            val currentTimeMillisLong = System.currentTimeMillis()
            timeInMillis = currentTimeMillisLong
            timeZone = TimeZone.getTimeZone("GMT+8")
            set(Calendar.MONTH,11)
            set(Calendar.DAY_OF_MONTH,31)
            set(Calendar.HOUR_OF_DAY,15)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            var selectLong = timeInMillis
            if (System.currentTimeMillis()>selectLong){
                set(Calendar.YEAR, DateUtils.getYear()+1)
                selectLong=timeInMillis
            }

            val intent = Intent(this@MainActivity, MyBroadcastReceiver::class.java)
            intent.action = Constants.ACTION_UPLOAD_YEAR
            val pendingIntent =if (Build.VERSION.SDK_INT >= 31)
                PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            else
                PendingIntent.getBroadcast(this@MainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, selectLong,
                date, pendingIntent
            )
        }
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
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
        SPUtil.putObj("${mUser?.accountId}PrivacyPassword","")

        MyApplication.mDaoSession?.clear()
        AppDaoManager.getInstance().clear()
        BookGreenDaoManager.getInstance().clear()
        CourseGreenDaoManager.getInstance().clear()
        DiaryDaoManager.getInstance().clear()
        FreeNoteDaoManager.getInstance().clear()
        ItemTypeDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()
        NoteDaoManager.getInstance().clear()
        RecordDaoManager.getInstance().clear()
        WallpaperDaoManager.getInstance().clear()
        DateEventDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.BOOK_DRAW_PATH))
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
            Constants.SETTING_DATA_EVENT->{
                clearSqlData()
            }
            //每天更新
            Constants.AUTO_UPLOAD_DAY_EVENT->{
                typeEvent=Constants.AUTO_UPLOAD_DAY_EVENT
                mQiniuPresenter.getToken()
            }
            //每年更新
            Constants.AUTO_UPLOAD_YEAR_EVENT->{
                typeEvent=Constants.AUTO_UPLOAD_YEAR_EVENT
                mQiniuPresenter.getToken()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

}