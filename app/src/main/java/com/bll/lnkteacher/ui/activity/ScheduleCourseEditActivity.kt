package com.bll.lnkteacher.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.ScheduleClassModuleDialog
import com.bll.lnkteacher.dialog.ScheduleCourseTimeSelectorDialog
import com.bll.lnkteacher.dialog.ScheduleItemDialog
import com.bll.lnkteacher.manager.ClassScheduleGreenDaoManager
import com.bll.lnkteacher.mvp.model.ClassScheduleBean
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.presenter.ClassGroupPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IClassGroupView
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_class_schedule_edit.grid
import kotlinx.android.synthetic.main.common_title.tv_btn_1
import kotlinx.android.synthetic.main.common_title.tv_btn_2

//课程表
class ScheduleCourseEditActivity : BaseAppCompatActivity(), IContractView.IQiniuView,IClassGroupView {
    private lateinit var mUploadPresenter : QiniuPresenter
    private lateinit var mPresenter:ClassGroupPresenter
    private var type=2
    private var classGroupId=0
    private var classGroupName=""
    private var mode = 0//0五天六节 1六天六节 2五天七节 3六天七节 4五天八节 5六天八节
    private var row = 0
    private var column =0
    private var selectLists = mutableListOf<ClassScheduleBean>()//已经选择了的课程
    private var lists= mutableListOf<ItemList>()

    private var timeWidth = 60
    private var weekHeight = 86
    private var lessonsWidth = 230
    private var dividerHeight = 52

    private var totalWidth = 1330
    private var totalHeight = 1150

    private var height = 120
    private var width = 210
    private var path=""

    private var isSave=false

    override fun onToken(token: String) {
        val commitPaths= mutableListOf<String>()
        commitPaths.add(FileAddress().getPathCourse("course")+"/course${classGroupId}.png")
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    path=urls[0]
                    mPresenter.uploadClassGroup(classGroupId,path)
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
        lists.add(ItemList(0,"自习"))
    }

    override fun onUploadSuccess() {
        for (item in DataBeanManager.classGroups){
            if (item.classGroupId==classGroupId){
                item.imageUrl=path
            }
        }
        val intent = Intent()
        intent.putExtra("path", path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_class_schedule_edit
    }

    override fun initData() {
        initChangeScreenData()
        classGroupId=intent.getIntExtra("classGroupId",0)
        classGroupName= intent.getStringExtra("classGroupName").toString()

        if (!SPUtil.getBoolean("refreshScheduleCourse")){
            ClassScheduleGreenDaoManager.getInstance().delete(type, classGroupId)
            SPUtil.putBoolean("refreshScheduleCourse",true)
        }

        mPresenter.getClassGroupSubject(classGroupId)
    }

    override fun initChangeScreenData() {
        mUploadPresenter = QiniuPresenter(this,getCurrentScreenPos())
        mPresenter=ClassGroupPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("$classGroupName  课程表编辑")
        showView(tv_btn_1,tv_btn_2)

        tv_btn_2.text="模板"
        tv_btn_2.setOnClickListener {
            ScheduleClassModuleDialog(this,DataBeanManager.scheduleCourseModules).builder().setOnClickListener {
                if (mode!=it){
                    mode=it
                    ClassScheduleGreenDaoManager.getInstance().editByTypeLists(type, classGroupId, mode)
                    selectLists.clear()
                    grid.removeAllViews()
                    setData()
                }
            }
        }

        tv_btn_1.text="保存"
        tv_btn_1?.setOnClickListener {
            if (selectLists.size == 0) return@setOnClickListener
            showLoading()
            ClassScheduleGreenDaoManager.getInstance().delete(type, classGroupId)
            ClassScheduleGreenDaoManager.getInstance().insertAll(selectLists)
            selectLists.clear()
            grid.removeAllViews()
            isSave=true
            setData()
            Handler().postDelayed({
                val path=FileAddress().getPathCourse("course")+"/course${classGroupId}.png"
                BitmapUtils.saveScreenShot(grid, path)
                mUploadPresenter.getToken(true)
            },500)
        }

        val oldCourses= ClassScheduleGreenDaoManager.getInstance().queryByTypeLists(type,classGroupId)
        if (oldCourses.isNotEmpty()){
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
                height=(totalHeight-weekHeight-dividerHeight)/9
            }
            1 -> {//六天六节课
                row = 11
                column = 8
                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height=(totalHeight-weekHeight-dividerHeight)/9
            }
            2 -> {//五天七节课
                row = 12
                column = 7
                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height=(totalHeight-weekHeight-dividerHeight)/10
            }
            3 -> {//六天七节课
                row = 12
                column = 8
                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height=(totalHeight-weekHeight-dividerHeight)/10
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

        val heightTime1=weekHeight + 5 * height
        val type=if (mode<2) 4 else 5
        val heightTime2=type * height

        val view = getDateView("上午")
        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(0, 6)
        params.width = timeWidth
        params.height = heightTime1
        params.columnSpec = GridLayout.spec(0, 1)
        grid.addView(view, params)

        val view1 = getDateView("下午")
        val params1 = GridLayout.LayoutParams()
        params1.rowSpec = GridLayout.spec(7, row - 7)
        params1.width = timeWidth
        params1.height = heightTime2
        params1.columnSpec = GridLayout.spec(0, 1)
        grid.addView(view1, params1)
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
                    "早读",
                    "第一节",
                    "第二节",
                    "第三节",
                    "第四节",
                    "",
                    "午读",
                    "第五节",
                    "第六节",
                    "课后"
                )
            }
            else -> {
                arrayOf(
                    "早读",
                    "第一节",
                    "第二节",
                    "第三节",
                    "第四节",
                    "",
                    "午读",
                    "第五节",
                    "第六节",
                    "第七节",
                    "课后"
                )
            }
        }
        for (i in 1 until row) {
            val id = "1$i".toInt()
            //根据id 查询是否已经存储了对应的时间
            val courseBean = ClassScheduleGreenDaoManager.getInstance().queryID(type,classGroupId,id)
            val index = i - 1

            val view = getLessonsTimeView(lessons[index])
            view.id = id

            val tvTime = view.findViewById<TextView>(R.id.tv_time)
            view.setOnClickListener {
                setTime(id,tvTime)
            }

            if (courseBean != null) {
                tvTime.text = courseBean.name
                selectLists.add(courseBean)//将已经存在的加入课程集合
            }
            else{
                //保存时绘图去除hint
                if (isSave)
                    tvTime.text=" "
            }

            val params = GridLayout.LayoutParams()
            params.width = lessonsWidth
            when (i) {
                6 -> {
                    params.rowSpec = GridLayout.spec(i, 1)
                    params.height = dividerHeight
                    params.columnSpec = GridLayout.spec(0, column)
                }
                else -> {
                    params.rowSpec = GridLayout.spec(i, 1)
                    params.height = height
                    params.columnSpec = GridLayout.spec(1, 1)
                }
            }
            grid.addView(view, params)
        }

    }


    //内容
    private fun addContentLayout() {
        for (i in 2 until column) {
            for (j in 1 until row) {
                var view: View?
                val id = (i.toString() + j.toString()).toInt()
                //根据textview id 查询是否已经存储了对应的课程
                val courseBean = ClassScheduleGreenDaoManager.getInstance().queryID(type,classGroupId,id)

                val lineView = getDividerView()
                lineView.id = id

                val contentView = getCourseContentView()
                contentView.id = id
                val tvContent = contentView.findViewById<TextView>(R.id.tv_name)
                val ivClear = contentView.findViewById<ImageView>(R.id.iv_clear)
                val tvAdd = contentView.findViewById<TextView>(R.id.tv_add)

                contentView.setOnClickListener {
                    ScheduleItemDialog(this,"科目选择",lists).builder().setOnDialogClickListener{ contentStr ->
                        setContent(id,tvContent,contentStr)
                        disMissView(tvAdd)
                        showView(ivClear)
                    }
                }
                ivClear.setOnClickListener {
                    selectLists.removeIf { item->item.viewId==id }
                    tvContent.text=""
                    showView(tvAdd)
                    disMissView(ivClear)
                }

                if (courseBean != null) {
                    disMissView(tvAdd)
                    showView(ivClear)
                    tvContent.text = courseBean.name
                    selectLists.add(courseBean)//将已经存在的加入课程集合
                }
                else{
                    showView(tvAdd)
                    disMissView(ivClear)
                }

                //保存时去除其他内容
                if (isSave)
                    disMissView(tvAdd,ivClear)

                view=when(j){
                    6->{
                        lineView
                    }
                    else->{
                        contentView
                    }
                }

                val params = GridLayout.LayoutParams()
                when (j) {
                    6 -> {
                        (view as TextView).text="午休"
                        view.setBackgroundResource(R.drawable.bg_course)
                        params.rowSpec = GridLayout.spec(j, 1)
                        params.width = width * (column - 2) + lessonsWidth + timeWidth
                        params.height = dividerHeight
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        view.setBackgroundResource(R.drawable.bg_course)
                        params.rowSpec = GridLayout.spec(j, 1)
                        params.width = width
                        params.height = height
                        params.columnSpec = GridLayout.spec(i, 1)
                        params.setGravity(Gravity.TOP)
                    }
                }
                grid.addView(view, params)
            }

        }
    }

    //获得第二列课节view
    private fun getLessonsTimeView(str: String): View {
        return layoutInflater.inflate(R.layout.common_schedule_class_time, null).also {
            it.findViewById<TextView>(R.id.tv_name).also { iv ->
                iv.text=str
            }
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }

    //获取星期
    private fun getWeekView(str: String): View {
        return TextView(this).apply {
            setTextColor(Color.BLACK)
            text=str
            textSize = 28f
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.bg_course)
        }
    }
    //获得课程
    private fun getCourseContentView(): View {
        return layoutInflater.inflate(R.layout.common_schedule_class_content, null)
    }

    //空白view
    private fun getDividerView(): TextView {
        return TextView(this).apply {
            setTextColor(Color.BLACK)
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(20, 0, 20, 0)
        }
    }

    //获得第一列 时间
    private fun getDateView(str: String): View {
        return TextView(this).apply {
            setTextColor(Color.BLACK)
            text=str
            textSize = 26f
            gravity = Gravity.CENTER
            setLineSpacing(1f,1.5f)
            setBackgroundResource(R.drawable.bg_course)
        }
    }

    //时间选择器
    private fun setTime(viewId: Int,tvStart: TextView) {
        ScheduleCourseTimeSelectorDialog(this).builder().setOnDateListener {
                startStr, endStr->

            tvStart.text = "$startStr~$endStr"

            val course = ClassScheduleBean().apply {
                type=this@ScheduleCourseEditActivity.type
                classGroupId=this@ScheduleCourseEditActivity.classGroupId
                name = "$startStr~$endStr"
                this.viewId = viewId
                mode = this@ScheduleCourseEditActivity.mode
            }
            selectLists.removeIf { item->item.viewId==viewId}
            selectLists.add(course)
        }
    }

    private fun setContent(viewId:Int,v: TextView,contentStr:String){
        v.text = contentStr

        val course = ClassScheduleBean().apply {
            type=this@ScheduleCourseEditActivity.type
            classGroupId=this@ScheduleCourseEditActivity.classGroupId
            this.viewId = viewId
            name = contentStr
            mode = this@ScheduleCourseEditActivity.mode
        }

        selectLists.removeIf { item->item.viewId==v.id }
        selectLists.add(course)

    }

}