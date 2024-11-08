package com.bll.lnkteacher.ui.activity.book

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.DownloadBookDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.mvp.model.book.BookStore
import com.bll.lnkteacher.mvp.model.book.BookStoreType
import com.bll.lnkteacher.mvp.presenter.BookStorePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.BookStoreAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.utils.zip.IZipCallback
import com.bll.lnkteacher.utils.zip.ZipUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.android.synthetic.main.ac_list_tab.rv_list
import kotlinx.android.synthetic.main.common_title.tv_course
import kotlinx.android.synthetic.main.common_title.tv_grade
import kotlinx.android.synthetic.main.common_title.tv_province
import kotlinx.android.synthetic.main.common_title.tv_semester
import kotlinx.android.synthetic.main.common_title.tv_type
import org.greenrobot.eventbus.EventBus
import java.io.File

class TextBookStoreActivity : BaseActivity(),
    IContractView.IBookStoreView {

    private var tabId = 0
    private var tabStr = ""
    private val presenter = BookStorePresenter(this)
    private var books = mutableListOf<Book>()
    private var mAdapter: BookStoreAdapter? = null

    private var provinceStr = ""
    private var gradeId=0
    private var semester=0
    private var courseId=0//科目
    private var subType=0//教库分类
    private var downloadBookDialog: DownloadBookDialog? = null
    private var mBook: Book? = null
    private var isZip=false

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
                4->{
                    book.id=null
                    book.bookName=book.title
                    book.subjectName=book.subject
                    book.bookDesc=book.desc
                }
                else->{
                    book.version=DataBeanManager.versions[book.version.toInt()-1].desc
                }
            }
        }
        mAdapter?.setNewData(books)
    }

    override fun onType(bookStoreType: BookStoreType) {
        if (!bookStoreType.bookLibType.isNullOrEmpty()){
            subTypeList.clear()
            val types=bookStoreType.bookLibType
            for (item in types){
                subTypeList.add(PopupBean(item.type,item.desc,types.indexOf(item)==0))
            }
            subType=subTypeList[0].id

            if (subjectList.size>0){
                initSelectorView()
                fetchData()
            }
        }
    }

    override fun buyBookSuccess() {
        mBook?.buyStatus = 1
        downloadBookDialog?.setChangeStatus()
        mAdapter?.notifyDataSetChanged()
    }


    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        pageSize=12

        tabList = DataBeanManager.textbookStoreType.toMutableList()
        tabStr = tabList[0]

        getSemester()
        semesterList=DataBeanManager.popupSemesters(semester)

        provinceStr= mUser?.schoolProvince.toString()
        for (i in DataBeanManager.provinces.indices){
            provinceList.add(PopupBean(i,DataBeanManager.provinces[i].value,DataBeanManager.provinces[i].value==provinceStr))
        }

        courseId=DataBeanManager.getCourseId(mUser?.subjectName!!)
        subjectList=DataBeanManager.popupCourses(courseId)

        if (DataBeanManager.getClassGroupsGrade()==0){
            gradeId=1
            gradeList=DataBeanManager.popupGrades(1)
        }
        else{
            gradeId=DataBeanManager.getClassGroupsGrade()
            gradeList=DataBeanManager.popupGrades(gradeId)
        }
        presenter.getBookType()
    }

    override fun initView() {
        setPageTitle("教材")
        showView(tv_province,tv_course,tv_grade,tv_semester)

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
                    tabStr =  if (subType==1) "教学期刊" else "教学教育"
                    tv_type.text = item.name
                    pageIndex = 1
                    fetchData()
                }
        }
    }

    private fun initTab() {
        for (i in tabList.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=tabList[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when(position){
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
        tabId = position
        isZip = !(tabId==0||tabId==1||tabId==4)
        tabStr =  if (subType==1&&tabId==4) "教学期刊" else tabList[position]
        pageIndex = 1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,30f),DP2PX.dip2px(this,50f),DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = BookStoreAdapter(R.layout.item_bookstore, books)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            mBook = books[position]
            showBookDetails(mBook!!)
        }
    }

    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: Book) {
        downloadBookDialog = DownloadBookDialog(this, book)
        downloadBookDialog?.builder()
        downloadBookDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                val localBook = BookGreenDaoManager.getInstance().queryTextBookByBookID(getHostType(book.type),book.bookId)
                if (localBook == null) {
                    showLoading()
                    downLoadStart(book.downloadUrl, book)
                } else {
                    book.loadSate = 2
                    showToast("已下载")
                    downloadBookDialog?.setDissBtn()
                    mAdapter?.notifyDataSetChanged()
                }
            } else {
                val map = HashMap<String, Any>()
                map["bookId"] = book.bookId
                when(tabId){
                    0,1->{
                        map["type"] = 2
                    }
                    2,3->{
                        map["type"] = 1
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
        val path=if (isZip){
            FileAddress().getPathZip(fileName)
        }
        else{
            FileAddress().getPathTextBook(fileName+MethodManager.getUrlFormat(book.downloadUrl))
        }
        val download = FileDownManager.with(this).create(url).setPath(path)
            .startSingleTaskDownLoad(object : FileDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M")+ "/"+
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            downloadBookDialog?.setUnClickBtn(s)
                        }
                    }
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }

                override fun completed(task: BaseDownloadTask?) {
                    if (!isZip){
                        book.bookPath = path
                        book.bookDrawPath=FileAddress().getPathTextBookDraw(fileName)
                        complete(book)
                    }
                    else{
                        val fileTargetPath = FileAddress().getPathHomeworkBook(fileName)
                        unzip(book, path, fileTargetPath)
                    }
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    //删除缓存 poolmap
                    hideLoading()
                    showToast("${book.bookName}下载失败")
                    downloadBookDialog?.setChangeStatus()
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
                book.bookDrawPath=FileAddress().getPathHomeworkBookDraw(File(fileTargetPath).name)
                book.bookPath = fileTargetPath
                //下载解压完成后更新存储的book
                complete(book)
                FileUtils.deleteFile(File(targetFileStr))
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onError(msg: String?) {
                hideLoading()
                showToast(book.bookName+msg!!)
                downloadBookDialog?.setChangeStatus()
            }
            override fun onStart() {
            }
        })
    }

    private fun complete(book: Book){
        book.apply {
            id=null
            loadSate = 2
            category = 0
            typeId=getHostType(book.type)
            subtypeStr=tabStr
            time = System.currentTimeMillis()
        }
        BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
        //更新列表
        mAdapter?.notifyDataSetChanged()
        downloadBookDialog?.dismiss()

        Handler().postDelayed({
            showToast(book.bookName+getString(R.string.book_download_success))
            hideLoading()
        },500)
    }

    /**
     * 得到课本主类型
     * type 1 教学期刊 2教学教育
     */
    private fun getHostType(type:Int):Int{
        return when(tabId){0,1->0 2,3->6 else->if(type==2)7 else 8}
    }

    /**
     * 设置课本学期（月份为9月份之前为下学期）
     */
    private fun getSemester(){
        semester=if (DateUtils.getMonth()<9) 2 else 1
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

    override fun onRefreshData() {
        presenter.getBookType()
    }

    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }
}