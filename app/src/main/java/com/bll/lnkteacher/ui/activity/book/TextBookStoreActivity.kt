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
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.DownloadTextbookDialog
import com.bll.lnkteacher.dialog.PopupCityList
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.book.BookStoreType
import com.bll.lnkteacher.mvp.model.book.TextbookBean
import com.bll.lnkteacher.mvp.model.book.TextbookStore
import com.bll.lnkteacher.mvp.presenter.BookStorePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TextbookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileBigDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
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

class TextBookStoreActivity : BaseAppCompatActivity(), IContractView.IBookStoreView {
    private var tabId = 0
    private var tabStr = ""
    private lateinit var presenter :BookStorePresenter
    private var textBooks = mutableListOf<TextbookBean>()
    private var mAdapter: TextbookAdapter? = null
    private var provinceStr = ""
    private var gradeId=0
    private var semester=0
    private var courseId=0//科目
    private var subType=0//教库分类
    private var downloadBookDialog: DownloadTextbookDialog? = null
    private var cityPopWindow:PopupCityList?=null
    private var subjectList = mutableListOf<PopupBean>()
    private var semesterList = mutableListOf<PopupBean>()
    private var gradeList = mutableListOf<PopupBean>()
    private var tabList = mutableListOf<String>()
    private var subTypeList = mutableListOf<PopupBean>()
    private var position=0

    override fun onTextBook(bookStore: TextbookStore) {
        setPageNumber(bookStore.total)
        textBooks = bookStore.list
        mAdapter?.setNewData(textBooks)
    }

    override fun onType(bookStoreType: BookStoreType) {
        if (!bookStoreType.bookLibType.isNullOrEmpty()){
            val types=bookStoreType.bookLibType
            for (item in types){
                subTypeList.add(PopupBean(item.type,item.desc,types.indexOf(item)==0))
            }
            subType=subTypeList[0].id

            initSelectorView()
            fetchData()
        }
    }

    override fun buyBookSuccess() {
        textBooks[position].buyStatus = 1
        mAdapter?.notifyItemChanged(position)
        downloadBookDialog?.setChangeStatus()
    }


    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12

        tabList = DataBeanManager.textbookStoreType.toMutableList()
        tabStr = tabList[0]

        getSemester()
        semesterList=DataBeanManager.popupSemesters(semester)

        provinceStr= mUser?.schoolCity.toString()

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

        if (NetworkUtil.isNetworkConnected()){
            presenter.getBookType()
        }
    }

    override fun initChangeScreenData() {
        presenter = BookStorePresenter(this)
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
            if (cityPopWindow==null){
                cityPopWindow=PopupCityList(this,tv_province,tv_province.width).builder()
                cityPopWindow?.setOnSelectListener { item ->
                    provinceStr = item.name
                    tv_province.text = item.name
                    pageIndex = 1
                    fetchData()
                }
            }
            else{
                cityPopWindow?.show()
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
        tabStr =  if (subType==1&&tabId==4) "专业期刊" else tabList[position]
        pageIndex = 1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,30f),DP2PX.dip2px(this,50f),DP2PX.dip2px(this,30f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = TextbookAdapter(R.layout.item_bookstore, textBooks)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco(4, 60))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            showBookDetails(textBooks[position])
        }
    }

    /**
     * 展示书籍详情
     */
    private fun showBookDetails(book: TextbookBean) {
        downloadBookDialog = DownloadTextbookDialog(this, book ,if (tabId==4) 2 else 1)
        downloadBookDialog?.builder()
        downloadBookDialog?.setOnClickListener {
            if (book.buyStatus == 1) {
                val localBook = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(getCategory(),book.bookId)
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
    private fun downLoadStart(url: String, book: TextbookBean): BaseDownloadTask? {
        val fileName = book.bookId.toString()//文件名
        val path=if (tabId<4){
            FileAddress().getPathZip(fileName)
        }
        else{
            FileAddress().getPathTeachingBook(fileName+MethodManager.getUrlFormat(book.downloadUrl))
        }
        val download = FileBigDownManager.with(this).create(url).setPath(path)
            .startSingleTaskDownLoad(object : FileBigDownManager.SingleTaskCallBack {

                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                    if (task != null && task.isRunning) {
                        runOnUiThread {
                            val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M")+ "/"+
                                    ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                            downloadBookDialog?.setUnClickBtn(s)
                        }
                    }
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    when(tabId){
                        0,1->{
                            book.bookPath =  FileAddress().getPathTextBook(fileName)
                            book.bookDrawPath=FileAddress().getPathTextBookDraw(fileName)
                            unzip(book, path, book.bookPath)
                        }
                        2,3->{
                            book.bookPath =  FileAddress().getPathHomeworkBook(fileName)
                            book.bookDrawPath=FileAddress().getPathHomeworkBookDraw(fileName)
                            unzip(book, path, book.bookPath)
                        }
                        4->{
                            book.bookPath = path
                            book.bookDrawPath=FileAddress().getPathTeachingBookDraw(fileName)
                            complete(book)
                        }
                    }
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
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
    private fun unzip(book: TextbookBean, targetFileStr: String, fileTargetPath: String) {
        ZipUtils.unzip(targetFileStr, fileTargetPath, object : IZipCallback {
            override fun onFinish() {
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

    private fun complete(book: TextbookBean){
        book.apply {
            id=null
            loadSate = 2
            category = this@TextBookStoreActivity.getCategory()
            time = System.currentTimeMillis()
        }
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
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
     * 获取主类型
     */
    private fun getCategory():Int{
        return when(tabId){
            0,1->{
                0
            }
            2,3->{
                1
            }
            else->{
                //教学教育2 专业期刊3
                if (textBooks[position].type==2) 2 else 3
            }
        }
    }

    /**
     * 设置课本学期（月份为9月份之前为下学期）
     */
    private fun getSemester(){
        semester=if (DateUtils.getMonth()<9) 2 else 1
    }

    override fun fetchData() {
        textBooks.clear()
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


    override fun onDestroy() {
        super.onDestroy()
        FileDownloader.getImpl().pauseAll()
    }
}