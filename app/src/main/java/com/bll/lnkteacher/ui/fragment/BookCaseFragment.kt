package com.bll.lnkteacher.ui.fragment

import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.Book
import com.bll.lnkteacher.ui.activity.book.BookTypeListActivity
import com.bll.lnkteacher.ui.adapter.BookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_bookcase.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    private var mAdapter: BookAdapter?=null
    private var position=0
    private var books= mutableListOf<Book>()//所有数据
    private var bookTopBean:Book?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(R.string.main_bookcase_title)

        initRecyclerView()
        findBook()

        tv_type.setOnClickListener {
            startActivity(Intent(activity, BookTypeListActivity::class.java))
        }

        ll_book_top.setOnClickListener {
            bookTopBean?.let { gotoBookDetails(it) }
        }
    }

    override fun lazyLoad() {
    }


    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4, DP2PX.dip2px(activity,23f),28))
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                gotoBookDetails(bookBean)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@BookCaseFragment.position=position
                delete()
                true
            }
        }
    }

    /**
     * 跳转阅读器
     */
    private fun gotoBookDetails(bookBean: Book){
        bookBean.isLook=true
        bookBean.time=System.currentTimeMillis()
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean)
        EventBus.getDefault().post(Constants.BOOK_EVENT)
        val intent = Intent()
        intent.action = "com.geniatech.reader.action.VIEW_BOOK_PATH"
        intent.setPackage("com.geniatech.knote.reader")
        intent.putExtra("path", bookBean.bookPath)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 1)
        startActivity(intent)
    }

    /**
     * 查找本地书籍
     */
    private fun findBook(){
        books=BookGreenDaoManager.getInstance().queryAllBook(true)
        if (books.size==0){
            bookTopBean=null
        }
        else{
            bookTopBean=books[0]
            books.removeFirst()
        }
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (bookTopBean!=null){
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_up)
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_down)
        }
        else{
            iv_content_up.setImageBitmap(null)
            iv_content_down.setImageBitmap(null)
        }
    }


    private fun setImageUrl(url: String,image: ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    //删除书架书籍
    private fun delete(){
        CommonDialog(requireActivity()).setContent(R.string.toast_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val book=books[position]
                BookGreenDaoManager.getInstance().deleteBook(book) //删除本地数据库
                books.remove(book)
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                if (File(book.bookDrawPath).exists())
                    FileUtils.deleteFile(File(book.bookDrawPath))
                mAdapter?.notifyDataSetChanged()

                if (books.size==11)
                {
                    findBook()
                }
            }
        })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag == Constants.BOOK_EVENT) {
            findBook()
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        findBook()
    }

}