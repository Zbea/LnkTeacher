package com.bll.lnkteacher.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MyBroadcastReceiver
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.AreaBean
import com.bll.lnkteacher.ui.adapter.MainListAdapter
import com.bll.lnkteacher.ui.fragment.*
import com.bll.lnkteacher.utils.FileUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_main.*
import java.util.*

class MainActivity : BaseActivity() {

    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var lastFragment: Fragment? = null

    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var groupManagerFragment: GroupManagerFragment? = null
    private var homeworkManagerFragment: HomeworkManagerFragment? = null
    private var noteFragment: NoteFragment? = null
    private var appFragment: AppFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var examFragment: ExamFragment? = null

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
        groupManagerFragment= GroupManagerFragment()
        homeworkManagerFragment = HomeworkManagerFragment()
        noteFragment= NoteFragment()
        appFragment = AppFragment()
        examFragment= ExamFragment()

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
                3 -> switchFragment(lastFragment, groupManagerFragment)
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

    //跳转笔记
    fun goToNote(){
        mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
        mHomeAdapter?.updateItem(5, true)//更新新的位置
        switchFragment(lastFragment, noteFragment)
        lastPosition=5
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

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode === KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }


}