package com.bll.lnkteacher.ui.activity.drawing

import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CatalogChildBean
import com.bll.lnkteacher.mvp.model.CatalogMsg
import com.bll.lnkteacher.mvp.model.CatalogParentBean
import com.bll.lnkteacher.mvp.model.book.Book
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File


class BookDetailsActivity:BaseDrawingActivity() {
    private var book: Book?=null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParentBean>()
    private var childItems = mutableListOf<CatalogChildBean>()
    private var pageStart=1
    private var page = 0 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id=intent.getIntExtra("book_id",0)
        val type=intent.getIntExtra("book_type",0)
        book = BookGreenDaoManager.getInstance().queryTextBookByBookID(type,id)
        if (book == null) return
        page=book?.pageIndex!!
        val cataLogFilePath =FileAddress().getPathTextBookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(cataLogFilePath))
        {
            val cataMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(cataLogFilePath)))
            try {
                catalogMsg = Gson().fromJson(cataMsgStr, CatalogMsg::class.java)
            } catch (e: Exception) {
            }
            if (catalogMsg!=null){
                for (item in catalogMsg?.contents!!) {
                    val catalogParent = CatalogParentBean()
                    catalogParent.title = item.title
                    catalogParent.pageNumber = item.pageNumber
                    catalogParent.picName = item.picName
                    for (ite in item.subItems) {
                        val catalogChild = CatalogChildBean()
                        catalogChild.title = ite.title
                        catalogChild.pageNumber = ite.pageNumber
                        catalogChild.picName = ite.picName
                        catalogParent.addSubItem(catalogChild)
                        childItems.add(catalogChild)
                    }
                    parentItems.add(catalogParent)
                    catalogs.add(catalogParent)
                }
            }
        }
    }

    override fun initView() {
        disMissView(iv_btn)
        if (catalogMsg!=null){
            pageCount =catalogMsg?.totalCount!!
            pageStart =catalogMsg?.startCount!!
        }
        onChangeContent()
    }

    override fun onCatalog() {
        CatalogDialog(this,screenPos, getCurrentScreenPos(),catalogs, 1, pageStart).builder().setOnDialogClickListener(object : CatalogDialog.OnDialogClickListener {
            override fun onClick(position: Int) {
                if (page!=position-1){
                    page = position - 1
                    onChangeContent()
                }
            }
            override fun onEdit(position: Int, title: String) {
            }
        })
    }

    override fun onPageDown() {
        if (isExpand){
            if (page<pageCount-2){
                page+=2
                onChangeContent()
            }
            else if (page==pageCount-2){
                page=pageCount-1
                onChangeContent()
            }
        }
        else{
            if (page<pageCount-1){
                page+=1
                onChangeContent()
            }
        }
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 1) {
                page -= 2
                onChangeContent()
            } else {
                page = 1
                onChangeContent()
            }
        } else {
            if (page > 0) {
                page -= 1
                onChangeContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onChangeContent()
    }

    override fun onChangeContent() {
        if (pageCount==0)
            return
        if (page>=pageCount){
            page=pageCount-1
            return
        }
        if (page==0&&isExpand){
            page=1
        }
        tv_page_total.text="${pageCount-pageStart}"
        tv_page_total_a.text="${pageCount-pageStart}"

        loadPicture(page, elik_b!!, v_content_b!!)
        tv_page.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
        if (isExpand){
            loadPicture(page-1, elik_a!!, v_content_a!!)
            if (screenPos==Constants.SCREEN_LEFT){
                tv_page.text = if (page-(pageStart-1)>0) "${page-(pageStart-1)}" else ""
                tv_page_a.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
            }
            if (screenPos== Constants.SCREEN_RIGHT){
                tv_page_a.text = if (page-(pageStart-1)>0) "${page-(pageStart-1)}" else ""
                tv_page.text = if (page+1-(pageStart-1)>0) "${page + 1-(pageStart-1)}" else ""
            }
        }
        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
    }

    //加载图片
    private fun loadPicture(index: Int,elik:EinkPWInterface,view:ImageView) {
        val showFile = getIndexFile(index)
        if (showFile!=null){
            book?.pageUrl=showFile.path //设置当前页面路径
            GlideUtils.setImageCacheUrl(this,showFile.path,view)
            val drawPath=book?.bookDrawPath+"/${index+1}.png"
            elik.setLoadFilePath(drawPath,true)
        }
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File? {
        val path=FileAddress().getPathTextBookPicture(book?.bookPath!!)
        val listFiles = FileUtils.getFiles(path)
        return if (listFiles.size>0) listFiles[index] else null
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time=System.currentTimeMillis()
        book?.pageIndex=page
        BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
            EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }



}