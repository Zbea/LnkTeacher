package com.bll.lnkteacher.ui.activity.book

import android.content.Intent
import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.SCREEN_LEFT
import com.bll.lnkteacher.Constants.Companion.SCREEN_RIGHT
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogBookDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.book.TextbookBean
import com.bll.lnkteacher.mvp.model.catalog.CatalogChildBean
import com.bll.lnkteacher.mvp.model.catalog.CatalogMsg
import com.bll.lnkteacher.mvp.model.catalog.CatalogParentBean
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_edit
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
    private var upPage=0//上一页

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
        else{
            pageCount=FileUtils.getFiles(FileAddress().getPathTextBookPicture(book?.bookPath!!)).size
        }
    }

    override fun initView() {
        showView(iv_edit)
        disMissView(iv_btn)

        iv_edit.setOnClickListener {
            customStartActivity(Intent(this,TextBookAnnotationActivity::class.java)
                .putExtra("path",getAnnotationPath())
                .putExtra( Constants.INTENT_SCREEN_LABEL, if (getCurrentScreenPos()== SCREEN_RIGHT) SCREEN_LEFT else SCREEN_RIGHT)
            )
        }

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
            if (page<pageCount-1){
                page+=2
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
            if (page > 0) {
                page -= 2
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
        if (page<0)
            page=0
        if (page>=pageCount){
            page=pageCount-1
        }
        if (page>pageCount-2&&isExpand)
            page=pageCount-2

        if (page!=upPage){
            upPage=page
            DataBeanManager.textBookAnnotationPath=getAnnotationPath()
            EventBus.getDefault().post(Constants.TEXTBOOK_ANNOTATION_CHANGE_PAGE_EVENT)
        }

        if (FileUtils.isExistContent(getAnnotationPath())){
            iv_edit.setImageResource(R.mipmap.icon_draw_annotation_exist)
        }
        else{
            iv_edit.setImageResource(R.mipmap.icon_draw_annotation)
        }

        if (isExpand){
            val page_up=page+1//上一页页码
            loadPicture(page, elik_a!!, v_content_a!!)
            loadPicture(page_up, elik_b!!, v_content_b!!)
            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page,tv_page_a,tv_page_total_a)
                setPageCurrent(page_up,tv_page,tv_page_total)
            }
            else{
                setPageCurrent(page,tv_page,tv_page_total)
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
        }
        else{
            loadPicture(page, elik_b!!, v_content_b!!)
            setPageCurrent(page,tv_page,tv_page_total)
        }
        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
    }

    private fun getAnnotationPath():String{
        val bookName= MD5Utils.digest(book?.bookId.toString())
        return FileAddress().getPathTextBookAnnotation(bookName,page+1)
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

        ActivityManager.getInstance().finishActivity(TextBookAnnotationActivity::class.java.name)
    }

}