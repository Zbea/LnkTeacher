package com.bll.lnkteacher.ui.activity

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CourseModuleDialog
import com.bll.lnkteacher.dialog.CourseTimeSelectorDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.manager.ClassScheduleGreenDaoManager
import com.bll.lnkteacher.mvp.model.ClassScheduleBean
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IClassGroupView
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import kotlinx.android.synthetic.main.ac_class_schedule.grid
import kotlinx.android.synthetic.main.common_title.tv_btn_1
import kotlinx.android.synthetic.main.common_title.tv_btn_2
import org.greenrobot.eventbus.EventBus

//课程表
class ClassScheduleActivity : BaseActivity(), IContractView.IFileUploadView,IClassGroupView {
    private lateinit var mUploadPresenter :FileUploadPresenter
    private lateinit var mPresenter:ClassGroupPresenter
    private var type=1
    private var classGroupId=0
    private var mode = 0//0五天六节 1六天六节 2五天七节 3六天七节 4五天八节 5六天八节
    private var row = 11
    private var column = 7
    private var selectLists = mutableListOf<ClassScheduleBean>()//已经选择了的课程
    private var lists= mutableListOf<ItemList>()

    private var timeWidth = 60
    private var weekHeight = 102
    private var lessonsWidth = 230
    private var dividerHeight = 52
    private var dividerHeight1 = 76

    private var totalWidth = 1330
    private var totalHeight = 1150

    private var height = 120
    private var width = 210

    override fun onToken(token: String) {
        val commitPaths= mutableListOf<String>()
        commitPaths.add(FileAddress().getPathCourse("course")+"/course${classGroupId}.png")
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    if (type==1){
                        mPresenter.uploadClassSchedule(urls[0])
                    }
                    else{
                        mPresenter.uploadClassGroup(classGroupId,urls[0])
                    }
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onSubjects(subjects: MutableList<String>) {
        for (subject in subjects){
            lists.add(ItemList(DataBeanManager.getCourseId(subject),subject))
        }
    }

    override fun onUploadSuccess() {
        if (type==1){
            EventBus.getDefault().post(Constants.COURSE_EVENT)
        }
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_class_schedule
    }

    override fun initData() {
        initChangeScreenData()
        type = intent.flags
        classGroupId=intent.getIntExtra("classGroupId",0)

        if (type==1){
            val classGroups=DataBeanManager.classGroups
            for (item in classGroups){
                if (item.state==1)
                    lists.add(ItemList(item.classId,item.name))
            }
        }
        else{
            mPresenter.getClassGroupSubject(classGroupId)
        }
    }

    override fun initChangeScreenData() {
        mUploadPresenter = FileUploadPresenter(this,getCurrentScreenPos())
        mPresenter=ClassGroupPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(if (type==1)"排课表   编辑" else "课程表    编辑")
        showView(tv_btn_1,tv_btn_2)

        tv_btn_2.text="模板"
        tv_btn_2.setOnClickListener {
            CourseModuleDialog(this).builder().setOnClickListener {
                mode=it
                ClassScheduleGreenDaoManager.getInstance().editByTypeLists(type, classGroupId, mode)
                selectLists.clear()
                grid.removeAllViews()
                setData()
            }
        }

        tv_btn_1.text="保存"
        tv_btn_1?.setOnClickListener {
            if (selectLists.size == 0) return@setOnClickListener
            ClassScheduleGreenDaoManager.getInstance().delete(type, classGroupId)
            ClassScheduleGreenDaoManager.getInstance().insertAll(selectLists)
            val path=FileAddress().getPathCourse("course")+"/course${classGroupId}.png"
            BitmapUtils.saveScreenShot(this, grid, path)
            mUploadPresenter.getToken(true)
        }

        val oldCourses= ClassScheduleGreenDaoManager.getInstance().queryByTypeLists(type,classGroupId)
        if (oldCourses.size>0){
            mode=oldCourses[0].mode
        }
        setData()
    }

    /**
     * 设置数据
     */
    private fun setData(){
        when (mode) {
            0 -> {//五天六节课
                row = 11
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 3) / 6

            }
            1 -> {//六天六节课
                row = 11
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 3) / 6

            }
            2 -> {//五天七节课
                row = 11
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 2) / 7
            }
            3 -> {//六天七节课
                row = 11
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 2) / 7

            }
            4 -> {//五天八节课
                row = 10
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1) / 8
            }
            5 -> {//六天八节课
                row = 10
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1) / 8
            }
        }
        grid.columnCount = column
        grid.rowCount = row


        addTimeLayout()
        addWeekLayout()
        addLessonsLayout()
        addContentLayout()
    }

    //添加时间布局在第一列
    private fun addTimeLayout() {

        val heightTime1: Int
        val heightTime2: Int
        when (mode) {
            0, 1 -> {
                heightTime1=weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight * 2 + 2 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec(if (mode==4||mode==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            2, 3 -> {
                heightTime1 =weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight + 3 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec(if (mode==4||mode==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            else -> {
                heightTime1 =weekHeight + 4 * height
                heightTime2=4 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 5)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec( 6 , row - 6)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
        }
    }

    //添加第一行星期几的布局
    private fun addWeekLayout() {
        val weeks = arrayOf(
            "课程",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六"
        )

        for (i in 1 until column) {
            val index = i - 1
            val view = getWeekView(weeks[index])

            val widths = if (i == 1) {//如果是第一个，则是课节的宽度
                lessonsWidth
            } else {
                width
            }

            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(0, 1)
            params.width = widths
            params.height = weekHeight
            params.columnSpec = GridLayout.spec(i, 1)
            grid.addView(view, params)
        }

    }

    //添加第二列课节布局
    private fun addLessonsLayout() {

        val lessons = when (mode) {
            0, 1 -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "",
                    "第六节",
                    ""
                )
            }
            2, 3 -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "第六节",
                    "第七节",
                    ""
                )
            }
            else -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "第六节",
                    "第七节",
                    "第八节"
                )
            }
        }
        for (i in 1 until row) {
            val id = "1$i".toInt()
            //根据id 查询是否已经存储了对应的时间
            val course = ClassScheduleGreenDaoManager.getInstance().queryID(type,classGroupId,id)
            val index = i - 1

            val view = getLessonsView(lessons[index])

            view.id = id
            val tvTime = view.findViewById<TextView>(R.id.tv_time)
            view.setOnClickListener {
                selectTime(tvTime, id)
            }

            if (course != null) {
                tvTime.text = course.name
                selectLists.add(course)//将已经存在的加入课程集合
            }

            val params = GridLayout.LayoutParams()
            params.width = lessonsWidth

            if (mode == 0 || mode == 1) {

                when (i) {
                    3, 8, 10 -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight
                        params.columnSpec = GridLayout.spec(1, column - 1)
                    }
                    6 -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
                    }
                }

            } else if (mode == 2 || mode == 3) {

                when (i) {
                    3, 10 -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight
                        params.columnSpec = GridLayout.spec(1, column - 1)
                    }
                    6 -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
                    }
                }

            } else {

                when (i) {
                    5 -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
                    }
                }

            }


            grid.addView(view, params)
        }

    }


    //内容
    private fun addContentLayout() {
        for (i in 2 until column) {
            for (j in 1 until row) {
                var view: TextView?
                val id = (i.toString() + j.toString()).toInt()
                //根据textview id 查询是否已经存储了对应的课程
                val course = ClassScheduleGreenDaoManager.getInstance().queryID(type,classGroupId,id)

                if (mode == 0 || mode == 1) {

                    if (j == 3 || j == 6 || j == 8 || j == 10) {
                        view = getCourseView1()
//                        view.setOnClickListener {
//                            inputContent(view as TextView)
//                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        3, 8, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)

                }
                else if (mode == 2 || mode == 3) {

                    if (j == 3 || j == 6 || j == 10) {
                        view = getCourseView1()
//                        view.setOnClickListener {
//                            inputContent(view as TextView)
//                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        3, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)

                }
                else {
                    if (j == 5) {
                        view = getCourseView1()
//                        view.setOnClickListener {
//                            inputContent(view as TextView)
//                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        5 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)
                }

                view.id = id

                if (course != null) {
                    view.text = course.name
                    selectLists.add(course)//将已经存在的加入课程集合
                }
            }

        }
    }

    //获得第二列课节view
    private fun getLessonsView(str: String): View {
        return layoutInflater.inflate(R.layout.common_course_lessons, null).also {
            it.findViewById<TextView>(R.id.tv_name).also { iv ->
                iv.text=str
            }
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }

    //获取星期
    private fun getWeekView(str: String): View {
        return TextView(this).also {
            it.setTextColor(Color.BLACK)
            it.text=str
            it.textSize = 28f
            it.gravity = Gravity.CENTER
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }
    //获得课程
    private fun getCourseView(): TextView {
        return TextView(this).apply {
            setTextColor(Color.BLACK)
            textSize = if (type==1)24f else 32f
            setLines(1)
            gravity = Gravity.CENTER
        }
    }

    //空白view
    private fun getCourseView1(): TextView {
        return TextView(this).apply {
            setTextColor(Color.BLACK)
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(20, 0, 20, 0)
        }
    }

    //获得第一列 时间
    private fun getDateView(str: String): View {
        return layoutInflater.inflate(R.layout.common_course_date, null).also {
            it.findViewById<TextView>(R.id.tv_name).also { tv ->
                tv.text=str
            }
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }

    //时间选择器
    private fun selectTime(tvStart: TextView, id: Int) {
        CourseTimeSelectorDialog(this).builder().setOnDateListener {
                startStr, endStr->

            tvStart.text = "$startStr~$endStr"

            val course = ClassScheduleBean().apply {
                type=this@ClassScheduleActivity.type
                classGroupId=this@ClassScheduleActivity.classGroupId
                name = "$startStr~$endStr"
                viewId = id
                mode = this@ClassScheduleActivity.mode
            }

            //删除已经存在了的
            if (selectLists.size > 0) {
                val it = selectLists.iterator()
                while (it.hasNext()) {
                    if (it.next().viewId == id) {
                        it.remove()
                    }
                }
            }
            selectLists.add(course)

        }
    }


    /**
     * 输入课程
     */
    private fun inputContent(v: TextView) {
        val titleStr=if (type==1) "班级选择" else "科目选择"
        ItemSelectorDialog(this,titleStr,lists).builder().setOnDialogClickListener{
            val contentStr=lists[it].name
            v.text = contentStr

            val course = ClassScheduleBean().apply {
                type=this@ClassScheduleActivity.type
                classGroupId=this@ClassScheduleActivity.classGroupId
                viewId = v.id
                name = contentStr
                mode = this@ClassScheduleActivity.mode
            }
            //删除已经存在了的
            if (selectLists.size > 0) {
                val it = selectLists.iterator()
                while (it.hasNext()) {
                    if (it.next().viewId == v.id) {
                        it.remove()
                    }
                }
            }
            selectLists.add(course)
        }
    }



}