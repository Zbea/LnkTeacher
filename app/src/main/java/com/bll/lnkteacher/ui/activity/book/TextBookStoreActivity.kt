package com.bll.lnkteacher.ui.activity.book

import android.os.Handler
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.BookDetailsDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.BookStore
import com.bll.lnkteacher.mvp.model.BookStoreType
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.BookStorePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.BookStoreAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.locks.ReentrantLock

class TextBookStoreActivity : BaseActivity(),
    IContractView.IBookStoreView {

    private var tabId = 0
    private var tabStr = ""
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<Book>()
    private var mAdapter: BookStoreAdapter? = null

    private var provinceStr = ""
    private var gradeId=0
    private var semester=0
    private var courseId=0//科目
    private var subType=0//教库分类
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var mBook: Book? = null

    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var tabList = mutableListOf<String>()
    private var subTypeList = mutableListOf<PopupBean>()

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        //修正数据
        for (book in books){
            when(tabId){
                0,1->{
                    book.version=DataBeanManager.versions[book.version.toInt()-1].desc
                }
                2,3->{
                    book.version=DataBeanManager.versions[book.version.toInt()-1].desc
                }
                4->{
                    book.id=null
                    book.bookName=book.title
                    book.subjectName=book.subject
                    book.bookDesc=book.desc
                }
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        if (!bookStoreType.bookLibType.isNullOrEmpty()){
            val types=bookStoreType.bookLibType
            for (item in types){
                subTypeList.add(PopupBean(item.type,item.desc,types.indexOf(item)==0))
            }
            subType=subTypeList[0].id

            if (subjectList.size>0){
                courseId=subjectList[0].id
                gradeId=gradeList[0].id
                initSelectorView()
                fetchData()
            }
        }
    }

    override fun buyBookSuccess() {
        mBook?.buyStatus = 1
        bookDetailsDialog?.setChangeStatus()
        mAdapter?.notifyDataSetChanged()
    }


    override fun layoutId(): Int {
        return R.layout.ac_bookstore
    }

    override fun initData() {
        pageSize=12

        tabList = DataBeanManager.textbookType.toMutableList()
        tabStr = tabList[0]
        tabList.removeLast()

        semesterList=DataBeanManager.popupSemesters
        semester= semesterList[0].id

        provinceStr= mUser?.schoolProvince.toString()
        for (i in DataBeanManager.provinces.indices){
            provinceList.add(PopupBean(i,DataBeanManager.provinces[i].value,DataBeanManager.provinces[i].value==provinceStr))
        }

        subjectList=DataBeanManager.popupCourses

        gradeList=DataBeanManager.popupGrades(1)

        presenter.getBookType()
    }

    override fun initView() {
        setPageTitle("教材")
        showView(tv_province,tv_course,tv_grade,tv_semester)

        mDialog?.setCanceledOutside(true)

        initRecyclerView()
        initTab()
    }


    /**
     * 设置分类选择
     */
    private fun initSelectorView() {
        tv_province.text = provinceStr
        tv_grade.text = DataBeanManager.getGradeStr(gradeId)
        tv_semester.text = DataBeanManager.popupSemesters[semester-1].name
        tv_course.text = DataBeanManager.getCourseStr(courseId)
        tv_type.text=subTypeList[0].name

        tv_grade.setOnClickListener {
            PopupRadioList(this, gradeList, tv_grade, tv_grade.width,5).builder()
               .setOnSelectListener { item ->
                gradeId = item.id
                tv_grade.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        tv_province.setOnClickListener {
            PopupRadioList(this, provinceList, tv_province,tv_province.width, 5).builder()
             .setOnSelectListener { item ->
                provinceStr = item.name
                tv_province.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        tv_semester.setOnClickListener {
            PopupRadioList(this, semesterList, tv_semester, tv_semester.width, 5).builder()
                .setOnSelectListener { item ->
                    tv_semester.text = item.name
                    semester=item.id
                    pageIndex = 1
                    fetchData()
                }
        }

        tv_course.setOnClickListener {
            PopupRadioList(this, subjectList, tv_course, tv_course.width, 5).builder()
                .setOnSelectListener { item ->
                    courseId = item.id
                    tv_course.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }

        tv_type.setOnClickListener {
            PopupRadioList(this, subTypeList, tv_type, tv_type.width, 5).builder()
                .setOnSelectListener { item ->
                    subType = item.id
                    tv_type.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }

    }


    //设置tab分类
    private fun initTab() {
        for (i in tabList.indices) {
            val radioButton =
                layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
            radioButton.id = i
            radioButton.text = tabList[i]
            radioButton.isChecked = i == 0
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                DP2PX.dip2px(this, 45f)
            )
            layoutParams.marginEnd = if (i == tabList.size - 1) 0 else DP2PX.dip2px(this, 44f)
            radioButton.layoutParams = layoutParams
            rg_group.addView(radioButton)
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                0,2->{
                    showView(tv_course,tv_grade,tv_semester,tv_province)
                    disMissView(tv_type)
                }
                1,3->{
                    showView(tv_grade,tv_course,tv_semester)
                    disMissView(tv_province,tv_type)
                }
                4->{
                    showView(tv_course,tv_type)
                    disMissView(tv_province,tv_grade,tv_semester)
                }
            }
            tabId = i
            tabStr = tabList[i]
            pageIndex = 1
            fetchData()
        }

    }

    /**
     * 得到课本主类型
     */
    private fun getHostType():Int{
        return when(tabId){0,1->0 2,3->6 else->7}
    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(this, 22f), 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            mBook = books[position]
            showBookDetails(mBook!!)
        }
    }

    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: Book) {
        bookDetailsDialog = BookDetailsDialog(this, book)
        bookDetailsDialog?.builder()
        bookDetailsDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                val localBook = BookGreenDaoManager.getInstance().queryTextBookByBookID(getHostType(),book.bookId)
                if (localBook == null) {
                    showLoading()
                    val downloadTask = downLoadStart(book.downloadUrl, book)
                    mDownMapPool[book.bookId] = downloadTask!!
                } else {
                    book.loadSate = 2
                    showToast("已下载")
                    bookDetailsDialog?.setDissBtn()
                    mAdapter?.notifyDataSetChanged()
                }
            } else {
                val map = HashMap<String, Any>()
                map["bookId"] = book.bookId
                when(tabId){
                    0,1->{
                        map["type"] = 1
                    }
                    2,3->{
                        map["type"] = 2
                    }
                    4->{
                        map["type"] = 6
                    }
                }
                presenter.buyBook(map)
            }
        }
    }

    /**
     * 下载解压书籍
     */
    private fun downLoadStart(url: String, book: Book): BaseDownloadTask? {

        val fileName = book.bookId.toString()//文件名
        val zipPath = FileAddress().getPathZip(fileName)
        val targetFile = File(zipPath)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val download = FileDownManager.with(this).create(url).setPath(zipPath)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning && task == mDownMapPool[book.bookId]) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M")+
                                    "/"+
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            bookDetailsDialog?.setUnClickBtn(s)
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    //删除缓存 poolmap
                    deleteDoneTask(task)
                    if (tabId==4){
                        val formatStr=book.downloadUrl.substring(book.downloadUrl.lastIndexOf("."))
                        val md5Name = MD5Utils.digest(book.bookId.toString())
                        val targetFileStr = FileAddress().getPathBook(md5Name+formatStr)
                        book.apply {
                            id=null
                            loadSate = 2
                            category = 0
                            typeId=getHostType()
                            subtypeStr=tabStr
                            time = System.currentTimeMillis()
                            bookPath = targetFileStr
                            bookDrawPath=FileAddress().getPathBookDraw(md5Name)
                        }
                        complete(book)
                    }
                    else{
                        lock.lock()
                        val fileTargetPath = FileAddress().getPathTextBook(fileName)
                        unzip(book, zipPath, fileTargetPath)
                        lock.unlock()
                    }
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast("${book.bookName}下载失败")
                    bookDetailsDialog?.setChangeStatus()
                    deleteDoneTask(task)
                }
            })
        return download
    }

    /**
     * 下载完成书籍解压
     */
    private fun unzip(book: Book, targetFileStr: String, fileTargetPath: String) {
        ZipUtils.unzip(targetFileStr, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
                book.apply {
                    loadSate = 2
                    category = 0
                    typeId=getHostType()
                    subtypeStr=tabStr
                    time = System.currentTimeMillis()//下载时间用于排序
                    bookPath = fileTargetPath
                    bookDrawPath=FileAddress().getPathTextBookDraw(File(fileTargetPath).name)
                }
                //下载解压完成后更新存储的book
                complete(book)
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onError(msg: String?) {
                hideLoading()
                showToast(book.bookName+msg!!)
                bookDetailsDialog?.setChangeStatus()
            }
            override fun onStart() {
            }
        })
    }

    private fun complete(book: Book){
        BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
        //更新列表
        mAdapter?.notifyDataSetChanged()
        bookDetailsDialog?.dismiss()

        Handler().postDelayed({
            showToast(book.bookName+getString(R.string.book_download_success))
            hideLoading()
        },500)
    }

    /**
     * 下载完成 需要删除列表
     */
    private fun deleteDoneTask(task: BaseDownloadTask?) {

        if (mDownMapPool.isNotEmpty()) {
            //拿出map中的键值对
            val entries = mDownMapPool.entries

            val iterator = entries.iterator();
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<*, *>
                val entity = entry.value
                if (task == entity) {
                    iterator.remove()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }

    override fun fetchData() {
        books.clear()
        mAdapter?.notifyDataSetChanged()

        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["subjectName"]=courseId
        when (tabId) {
            0->{
                map["type"] = 1
                map["area"] = provinceStr
                map["grade"] = gradeId
                map["semester"]=semester
                presenter.getTextBooks(map)
            }
            1->{
                map["type"] = 2
                map["grade"] = gradeId
                map["semester"]=semester
                presenter.getTextBooks(map)
            }
            2->{
                map["type"] = 1
                map["area"] = provinceStr
                map["grade"] = gradeId
                map["semester"]=semester
                presenter.getHomeworkBooks(map)
            }
            3->{
                map["type"] = 2
                map["grade"] = gradeId
                map["semester"]=semester
                presenter.getHomeworkBooks(map)
            }
            4->{
                map["subject"]=courseId
                map["type"]=subType
                presenter.getTeachingBooks(map)
            }
        }
    }
}