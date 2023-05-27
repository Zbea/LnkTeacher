package com.bll.lnkteacher.ui.activity

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
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.presenter.BookStorePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.BookStoreAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ZipUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_bookstore.*
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat
import java.util.concurrent.locks.ReentrantLock

class BookStoreActivity : BaseActivity(),
    IContractView.IBookStoreView {

    private var typeId = 0 //书籍分类
    private val mDownMapPool = HashMap<Int, BaseDownloadTask>()//下载管理
    private val lock = ReentrantLock()
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<Book>()
    private var mAdapter: BookStoreAdapter? = null

    private var provinceStr = ""
    private var gradeStr = ""
    private var typeStr = ""
    private var semesterStr=""
    private var courseId=0//科目
    private var bookDetailsDialog: BookDetailsDialog? = null
    private var mBook: Book? = null

    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var provinceList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var typeList = mutableListOf<String>()

    override fun onBook(bookStore: BookStore) {
        setPageNumber(bookStore.total)
        books = bookStore.list
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType?) {
        //年级分类
        if (bookStoreType?.grade.isNullOrEmpty()) return
        for (i in bookStoreType?.grade?.indices!!) {
            gradeList.add(
                PopupBean(i, bookStoreType?.grade[i], i == 0)
            )
        }
        gradeStr = gradeList[0].name

        for (i in bookStoreType.subjectList.indices) {
            val item=bookStoreType.subjectList[i]
            subjectList.add(PopupBean(item.type, item.desc, i == 0))
        }
        courseId = subjectList[0].id

        initSelectorView()
        getDataBook()
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
        provinceStr = mUser?.addr!!.split(",")[0]
        //获取地区分类
        val citysStr = FileUtils.readFileContent(resources.assets.open("city.json"))
        val cityBean = Gson().fromJson(citysStr, CityBean::class.java)
        for (i in cityBean.provinces.indices) {
            provinceList.add(
                PopupBean(i, cityBean.provinces[i].provinceName, cityBean.provinces[i].provinceName == provinceStr)
            )
        }

        typeList = DataBeanManager.textbookType.toMutableList()
        typeStr = typeList[0]
        typeList.removeAt(3)

        semesterList=DataBeanManager.semesters
        semesterStr= semesterList[0].name

        getData()
    }

    override fun initView() {
        setPageTitle("教材")

        initRecyclerView()
        initTab()
    }

    //获取数据
    private fun getData() {
        presenter.getBookType()
    }

    //获取教材
    private fun getDataBook() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["area"] = provinceStr
        map["grade"] = gradeStr
        map["type"] = typeStr
        map["subjectName"]=courseId
        map["semester"]=semesterStr
        presenter.getTextBooks(map)
    }

    //获取参考
    private fun getDataBookCk() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["grade"] = gradeStr
        map["semester"]=semesterStr
        map["subjectName"]=courseId
        presenter.getTextBookCks(map)
    }

    /**
     * 设置分类选择
     */
    private fun initSelectorView() {
        tv_province.text = provinceList[0].name
        tv_grade.text = gradeList[0].name
        tv_semester.text = semesterStr
        tv_course.text = subjectList[0].name

        tv_grade.setOnClickListener {
            PopupRadioList(this, gradeList, tv_grade, tv_grade.width,5).builder()
               .setOnSelectListener { item ->
                gradeStr = item.name
                tv_grade.text = gradeStr
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
                    semesterStr = item.name
                    tv_semester.text = item.name
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

    }


    //设置tab分类
    private fun initTab() {
        for (i in typeList.indices) {
            val radioButton =
                layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
            radioButton.id = i
            radioButton.text = typeList[i]
            radioButton.isChecked = i == 0
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                DP2PX.dip2px(this, 45f)
            )
            layoutParams.marginEnd = if (i == typeList.size - 1) 0 else DP2PX.dip2px(this, 44f)
            radioButton.layoutParams = layoutParams
            rg_group.addView(radioButton)
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            typeId = i
            typeStr = typeList[typeId]
            pageIndex = 1
            fetchData()
        }

    }

    private fun initRecyclerView() {
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
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
                val localBook = BookGreenDaoManager.getInstance().queryBookByBookID(book.bookId)
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
                map["type"] = if (typeId == 2) 1 else 2
                map["bookId"] = book.bookId
                presenter.buyBook(map)
            }
        }
    }

    /**
     * 下载解压书籍
     */
    private fun downLoadStart(url: String, book: Book): BaseDownloadTask? {

        val fileName = book?.bookId.toString()//文件名
        val targetFileStr = FileAddress().getPathZip(fileName)
        val download = FileDownManager.with(this).create(url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning && task == mDownMapPool[book?.bookId]) {
                        runOnUiThread {
                            val s = getFormatNum(
                                soFarBytes.toDouble() / (1024 * 1024),
                                "0.0"
                            ) + "M/" + getFormatNum(
                                totalBytes.toDouble() / (1024 * 1024),
                                "0.0"
                            ) + "M"
                            if (bookDetailsDialog != null)
                                bookDetailsDialog?.setUnClickBtn(s)
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    //删除缓存 poolmap
                    deleteDoneTask(task)
                    lock.lock()
                    unzip(book, targetFileStr, fileName)
                    mDialog?.dismiss()
                    lock.unlock()
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    mDialog?.dismiss()
                    showToast("${book.bookName}下载失败")
                    deleteDoneTask(task)
                }
            })
        return download
    }

    /**
     * 下载完成书籍解压
     */
    private fun unzip(book: Book, targetFileStr: String, fileName: String) {
        ZipUtils.unzip(targetFileStr, fileName, object : ZipUtils.ZipCallback {
            override fun onFinish(success: Boolean) {
                if (success) {
                    book.loadSate = 2
                    book.category = 0
                    book.textBookType = typeStr
                    book.time = System.currentTimeMillis()//下载时间用于排序
                    book.bookPath = FileAddress().getPathBook(fileName)
                    //下载解压完成后更新存储的book
                    BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                    EventBus.getDefault().post(TEXT_BOOK_EVENT)
                    //更新列表
                    mAdapter?.notifyDataSetChanged()
                    bookDetailsDialog?.dismiss()

                    Handler().postDelayed({
                        showToast("${book.bookName}下载完成")
                    },500)

                } else {
                    showToast("${book.bookName}解压失败")
                }
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onError(msg: String?) {
                showToast(msg!!)
            }

            override fun onStart() {
            }

        })
    }

    fun getFormatNum(pi: Double, format: String?): String? {
        val df = DecimalFormat(format)
        return df.format(pi)
    }

    /**
     * 下载完成 需要删除列表
     */
    private fun deleteDoneTask(task: BaseDownloadTask?) {

        if (mDownMapPool != null && mDownMapPool.isNotEmpty()) {
            //拿出map中的键值对
            val entries = mDownMapPool.entries

            val iterator = entries.iterator();
            while (iterator.hasNext()) {
                val entry = iterator.next() as Map.Entry<Long, BaseDownloadTask>
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
        when (typeId) {
            0, 1 -> {
                getDataBook()
            }
            else -> {
                getDataBookCk()
            }
        }
    }

}