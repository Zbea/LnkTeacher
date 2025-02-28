package com.bll.lnkteacher.ui.activity.book

import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogBookDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CatalogChildBean
import com.bll.lnkteacher.mvp.model.CatalogMsg
import com.bll.lnkteacher.mvp.model.CatalogParentBean
import com.bll.lnkteacher.mvp.model.book.TextbookBean
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


class TextbookDetailsActivity:BaseDrawingActivity() {
    private var book: TextbookBean?=null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParentBean>()
    private var childItems = mutableListOf<CatalogChildBean>()
    private var startCount=0
    private var page = 0 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val id=intent.getIntExtra("book_id",0)
        val type=intent.getIntExtra("book_type",0)
        book = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(type,id)
        if (book == null) return
        page=book?.pageIndex!!
        val cataLogFilePath =FileAddress().getPathTextBookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(cataLogFilePath))
        {
            val cataMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(cataLogFilePath)))
            catalogMsg = Gson().fromJson(cataMsgStr, CatalogMsg::class.java)
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
                pageCount =catalogMsg?.totalCount!!
                startCount = if (catalogMsg?.startCount!!-1<0)0 else catalogMsg?.startCount!!-1
            }
        }
    }

    override fun initView() {
        disMissView(iv_btn)
        onChangeContent()
    }

    override fun onCatalog() {
        CatalogBookDialog(this,screenPos, getCurrentScreenPos(),catalogs, startCount).builder().setOnDialogClickListener { pageNumber ->
            if (page != pageNumber - 1) {
                page = pageNumber - 1
                onChangeContent()
            }
        }
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

        loadPicture(page, elik_b!!, v_content_b!!)
        setPageCurrent(page,tv_page,tv_page_total)
        if (isExpand){
            val page_up=page-1//上一页页码
            loadPicture(page_up, elik_a!!, v_content_a!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
            else{
                setPageCurrent(page_up,tv_page,tv_page_total)
                setPageCurrent(page,tv_page_a,tv_page_total_a)
            }
        }
        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
    }

    /**
     * 设置当前页面页码
     */
    private fun setPageCurrent(currentPage:Int, tvPage: TextView, tvPageTotal: TextView){
        tvPage.text = if (currentPage>=startCount) "${currentPage-startCount+1}" else ""
        tvPageTotal.text=if (currentPage>=startCount) "${pageCount-startCount}" else ""
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
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

}