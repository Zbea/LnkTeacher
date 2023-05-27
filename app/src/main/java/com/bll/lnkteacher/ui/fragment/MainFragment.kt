package com.bll.lnkteacher.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.CLASSGROUP_EVENT
import com.bll.lnkteacher.Constants.Companion.COURSE_EVENT
import com.bll.lnkteacher.Constants.Companion.DATE_EVENT
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_BOOK_MANAGER_EVENT
import com.bll.lnkteacher.Constants.Companion.NOTE_EVENT
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CourseModuleDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.manager.NotebookDaoManager
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.presenter.MessagePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.*
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.ui.adapter.MainClassGroupAdapter
import com.bll.lnkteacher.ui.adapter.MainMessageAdapter
import com.bll.lnkteacher.ui.adapter.MainNoteAdapter
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date


/**
 * 首页
 */
class MainFragment : BaseFragment(),IContractView.IMessageView{


    private var mMessagePresenter=MessagePresenter(this)
    private var mBookAdapter: BookAdapter? = null
    private var textbooks= mutableListOf<Book>()
    private var mTeachingAdapter: MainClassGroupAdapter? = null
    private var classGroups= mutableListOf<ClassGroup>()

    private var notes= mutableListOf<Notebook>()
    private var mainNoteAdapter: MainNoteAdapter? = null
    private var messages= mutableListOf<MessageBean>()
    private var mMessageAdapter:MainMessageAdapter?=null

    override fun onList(message: Message) {
        messages=message.list
        mMessageAdapter?.setNewData(messages)
    }
    override fun onSend() {
    }
    override fun onDeleteSuccess() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle(R.string.main_home_title)
        showSearch(false)

        onClickView()

        initDateView()
        initMessageView()
        initTextBookView()
        initTeachingView()
        initCourse()
        initNote()

        findNotes()
        findTeaching()
        findTextBook()
    }

    override fun lazyLoad() {
        findMessages()
        fetchCommonData()
    }

    @SuppressLint("WrongConstant")
    private fun onClickView() {
        ll_date.setOnClickListener {
            startActivity(Intent(activity, DateActivity::class.java))
        }

        ll_message.setOnClickListener {
            startActivity(Intent(activity, MessageListActivity::class.java))
        }

        ll_note.setOnClickListener {
            (activity as MainActivity).goToNote()
        }

        ll_course.setOnClickListener {
            val courseType = SPUtil.getInt("courseType")
            startActivity(Intent(activity, MainCourseActivity::class.java).setFlags(1)
                .putExtra("courseType", courseType)
            )
        }

    }

    //课程表相关处理
    @SuppressLint("WrongConstant")
    private fun initCourse() {
        GlideUtils.setImageNoCacheUrl(activity,Constants.SCREEN_PATH + "/course.png",iv_course)

        iv_course_more.setOnClickListener {
            CourseModuleDialog(requireActivity()).builder().setOnClickListener { type ->
                startActivity(
                    Intent(activity, MainCourseActivity::class.java).setFlags(0)
                        .putExtra("courseType", type)
                )
            }
        }

    }

    //日历相关内容设置
    private fun initDateView() {

        tv_date_today.text = SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date())
        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(Date().time))+"/draw.png"
        GlideUtils.setImageNoCacheUrl(activity,path,iv_image)
    }

    //消息相关处理
    private fun initMessageView() {
        rv_main_message.layoutManager = LinearLayoutManager(activity)//创建布局管理
        mMessageAdapter=MainMessageAdapter(R.layout.item_main_message, null).apply {
            rv_main_message.adapter = this
            bindToRecyclerView(rv_main_message)
        }

    }

    //我的课本
    private fun initTextBookView() {
        rv_main_book.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mBookAdapter = BookAdapter(R.layout.item_main_book, textbooks)
        rv_main_book.adapter = mBookAdapter
        mBookAdapter?.bindToRecyclerView(rv_main_book)

    }

    //教学进度
    private fun initTeachingView() {

        rv_main_teaching.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mTeachingAdapter = MainClassGroupAdapter(R.layout.item_main_classgroup, null)
        rv_main_teaching.adapter = mTeachingAdapter
        mTeachingAdapter?.bindToRecyclerView(rv_main_teaching)
        rv_main_teaching.addItemDecoration(SpaceGridItemDeco(3, 25))
        mTeachingAdapter?.setOnItemClickListener { _, _, position ->
            val intent = Intent(activity, MainTeachingPlanActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("classGroup", classGroups[position])
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

    }

    //笔记
    private fun initNote(){

        mainNoteAdapter = MainNoteAdapter(R.layout.item_main_note, notes)
        rv_main_note.layoutManager = LinearLayoutManager(activity)//创建布局管理
        rv_main_note.adapter = mainNoteAdapter
        mainNoteAdapter?.bindToRecyclerView(rv_main_note)
        mainNoteAdapter?.setEmptyView(R.layout.common_empty)
        mainNoteAdapter?.setOnItemClickListener { adapter, view, position ->
            //跳转手绘
            val intent=Intent(activity, NoteDrawingActivity::class.java)
            val bundle= Bundle()
            bundle.putSerializable("note",notes[position])
            intent.putExtra("notes",bundle)
            startActivity(intent)
        }

    }

    /**
     * 查找课本
     */
    private fun findTextBook(){
        textbooks=BookGreenDaoManager.getInstance().queryAllTextBook(0,getString(R.string.textbook_tab_text))
        mBookAdapter?.setNewData(textbooks)
    }

    /**
     * 查找教学
     */
    private fun findTeaching(){
        classGroups=DataBeanManager.classGroups
        mTeachingAdapter?.setNewData(classGroups)
    }

    /**
     * 查找笔记
     */
    private fun findNotes(){
        notes= NotebookDaoManager.getInstance().queryAll()
        if (notes.size>6){
            notes=notes.subList(0,6)
        }
        mainNoteAdapter?.setNewData(notes)
    }

    private fun findMessages(){
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=4
        map["type"]=1
        mMessagePresenter.getList(map,false)
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        when (msgFlag) {
            DATE_EVENT -> {
                initDateView()
            }
            COURSE_EVENT -> {
                initCourse()
            }
            NOTE_BOOK_MANAGER_EVENT -> {
                findNotes() //用于删除笔记本后 刷新列表
            }
            NOTE_EVENT -> {
                findNotes()
            }
            CLASSGROUP_EVENT -> {
                findTeaching()
            }
            TEXT_BOOK_EVENT -> {
                findTextBook()
            }
            MESSAGE_EVENT -> {
                findMessages()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRefreshData() {
        lazyLoad()
    }

    override fun onClassGroupEvent() {
        findTeaching()
    }

}