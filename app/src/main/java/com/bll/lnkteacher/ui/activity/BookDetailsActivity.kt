package com.bll.lnkteacher.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.widget.ImageView
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.DrawingCatalogDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.mvp.model.CatalogChildBean
import com.bll.lnkteacher.mvp.model.CatalogMsg
import com.bll.lnkteacher.mvp.model.CatalogParentBean
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class BookDetailsActivity:BaseActivity() {
    private var book: Book?=null
    private var catalogMsg: CatalogMsg?=null
    private var catalogs= mutableListOf<MultiItemEntity>()
    private var parentItems= mutableListOf<CatalogParentBean>()
    private var childItems= mutableListOf<CatalogChildBean>()

    private var pageCount = 1
    private var page = 1 //当前页码

    private var elik_a: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_book_details
    }

    override fun initData() {
        val id=intent.getIntExtra("book_id",0)
        book = BookGreenDaoManager.getInstance().queryBookByBookID(id)
        page=book?.pageIndex!!
        if (book==null) return
        val cataLogFilePath =FileAddress().getPathBookCatalog(book?.bookPath!!)
        val cataMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(cataLogFilePath)))
        catalogMsg=Gson().fromJson(cataMsgStr, CatalogMsg::class.java)
        if (catalogMsg==null) return

        for (item in catalogMsg?.contents!!)
        {
            var catalogParentBean=CatalogParentBean()
            catalogParentBean.title=item.title
            catalogParentBean.pageNumber=item.pageNumber
            catalogParentBean.picName=item.picName
            for (ite in item.subItems){
                var catalogChildBean= CatalogChildBean()
                catalogChildBean.title=ite.title
                catalogChildBean.pageNumber=ite.pageNumber
                catalogChildBean.picName=ite.picName

                catalogParentBean.addSubItem(catalogChildBean)
                childItems.add(catalogChildBean)
            }
            parentItems.add(catalogParentBean)
            catalogs.add(catalogParentBean)
        }
    }

    override fun initView() {
        if (catalogMsg!=null){
            setPageTitle(catalogMsg?.title!!)
            pageCount=catalogMsg?.totalCount!!
        }

        elik_a=v_content_a.pwInterFace

        updateScreen()

        bindClick()
    }



    private fun bindClick(){

        iv_catalog.setOnClickListener {
            DrawingCatalogDialog(this,catalogs,1).builder()?.
            setOnDialogClickListener { position ->
                page = position
                updateScreen()
            }

        }

        btn_page_up.setOnClickListener {
            if (page>1){
                page-=1
                updateScreen()
            }
        }

        btn_page_down.setOnClickListener {
            if(page<pageCount){
                page+=1

                updateScreen()
            }
        }
    }


    //单屏翻页
    private fun updateScreen(){
        tv_page.text="$page/$pageCount"
        loadPicture(page,elik_a!!,v_content_a)
    }


    //加载图片
    private fun loadPicture(index: Int,elik:EinkPWInterface,view:ImageView) {
        val showFile = getIndexFile(index)
        if (showFile!=null){
            book?.pageUrl=showFile?.path //设置当前页面路径
            if (index>1){
                book?.pageUpUrl=getIndexFile(index-1)?.path
            }

            GlideUtils.setImageFile(this,showFile,view)

            val drawPath=showFile.path.replace(".jpg",".tch")
            elik?.setLoadFilePath(drawPath,true)
            elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
                override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
                }

                override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
                }

                override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                    elik?.saveBitmap(true) {}
                }

            })
        }
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File? {
        val path=FileAddress().getPathBookPicture(book?.bookPath!!)
        val listFiles = FileUtils.getFiles(path,".jpg")
        if (listFiles.size==0)
            return null
        return listFiles[index - 1]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time=System.currentTimeMillis()
        book?.pageIndex=page
        BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
            EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }



}