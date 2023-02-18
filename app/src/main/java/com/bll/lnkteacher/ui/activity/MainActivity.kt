package com.bll.lnkteacher.ui.activity

import android.content.Intent
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.MainListBean
import com.bll.lnkteacher.ui.adapter.MainListAdapter
import com.bll.lnkteacher.ui.fragment.*
import kotlinx.android.synthetic.main.ac_main.*

class MainActivity : BaseActivity() {

    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var mData= mutableListOf<MainListBean>()
    private var lastFragment: Fragment? = null

    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var groupManagerFragment: GroupManagerFragment? = null
    private var teachingFragment: TeachingFragment? = null
    private var noteFragment: NoteFragment? = null
    private var appFragment: AppFragment? = null
    private var textbookFragment: TextbookFragment? = null

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        mData= DataBeanManager.getIndexData()
    }


    override fun initView() {

        mainFragment = MainFragment()
        textbookFragment= TextbookFragment()
        bookcaseFragment = BookCaseFragment()
        groupManagerFragment= GroupManagerFragment()
        teachingFragment = TeachingFragment()
        noteFragment= NoteFragment()
        appFragment = AppFragment()

        switchFragment(lastFragment, mainFragment)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mHomeAdapter = MainListAdapter(R.layout.item_main_list, mData)
        rv_list.adapter = mHomeAdapter
        mHomeAdapter?.bindToRecyclerView(rv_list)
        mHomeAdapter?.setOnItemClickListener { adapter, view, position ->

            mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
            mHomeAdapter?.updateItem(position, true)//更新新的位置

            when (position) {
                0 -> switchFragment(lastFragment, mainFragment)
                1 -> switchFragment(lastFragment, textbookFragment)
                2 -> switchFragment(lastFragment, groupManagerFragment)
                3 -> switchFragment(lastFragment, teachingFragment)
                4 -> switchFragment(lastFragment, noteFragment)
                5 -> switchFragment(lastFragment, bookcaseFragment)
                6 -> switchFragment(lastFragment, appFragment)
            }

            lastPosition=position

        }

        iv_user.setOnClickListener {
            startActivity(Intent(this,AccountInfoActivity::class.java))
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
        return if (event.getKeyCode() === KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }


}