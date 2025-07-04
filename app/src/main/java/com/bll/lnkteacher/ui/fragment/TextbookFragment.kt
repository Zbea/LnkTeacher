package com.bll.lnkteacher.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants.Companion.AUTO_REFRESH_EVENT
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.book.TextbookBean
import com.bll.lnkteacher.mvp.presenter.TextbookPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.TextbookAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list.rv_list
import org.greenrobot.eventbus.EventBus

class TextbookFragment : BaseMainFragment(), IContractView.ITextbookView {
    private val mPresenter = TextbookPresenter(this,1)
    private var mAdapter: TextbookAdapter? = null
    private var books = mutableListOf<TextbookBean>()
    private var typeId = 0//用来区分课本类型
    private var position = 0

    override fun onAddHomeworkBook() {
        showToast(1,"设置教辅书成功")
        books[position].isHomework = true
        mAdapter?.notifyItemChanged(position)
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(books[position])
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list
    }

    override fun initView() {
        setTitle(DataBeanManager.getIndexLeftData()[2].name)
        pageSize = 9

        initRecyclerView()

        initTab()
    }

    override fun lazyLoad() {
    }

    private fun initTab() {
        val strs=DataBeanManager.textbookType
        for (i in strs.indices){
            itemTabTypes.add(ItemTypeBean().apply {
                title=strs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
        fetchData()
    }

    override fun onTabClickListener(view: View, position: Int) {
        typeId=position
        pageIndex=1
        fetchData()
    }

    private fun initRecyclerView() {
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(requireActivity(),20f), DP2PX.dip2px(requireActivity(),40f), DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(activity, 3)//创建布局管理
        mAdapter = TextbookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco(3,  40))
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val book = books[position]
            if (typeId<2) {
                MethodManager.gotoTextBookDetails(requireActivity(),book)
            } else {
                MethodManager.gotoTeachingDetails(requireActivity(),book)
            }
        }
        mAdapter?.onItemLongClickListener =
            BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this.position = position
                onLongClick()
                true
            }

    }

    //长按显示课本管理
    private fun onLongClick() {
        val book=books[position]
        //题卷本可以设置为作业
        val beans = mutableListOf<ItemList>()
        when(typeId){
            0->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
            }
            1->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
                beans.add(ItemList().apply {
                    name = "置顶"
                    resId = R.mipmap.icon_setting_top
                })
                beans.add(ItemList().apply {
                    name = "设为作业"
                    resId = R.mipmap.icon_setting_set
                })
            }
            else->{
                beans.add(ItemList().apply {
                    name = "删除"
                    resId = R.mipmap.icon_setting_delete
                })
                beans.add(ItemList().apply {
                    name = "置顶"
                    resId = R.mipmap.icon_setting_top
                })
            }
        }
        LongClickManageDialog(requireActivity(),1, book.bookName, beans).builder()
            .setOnDialogClickListener {
                when(it){
                    0->{
                        MethodManager.deleteTextbook(book)
                    }
                    1->{
                        book.time=System.currentTimeMillis()
                        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                        pageIndex=1
                        fetchData()
                    }
                    2->{
                        if (!book.isHomework) {
                            val map = HashMap<String, Any>()
                            map["name"] = book.bookName
                            map["type"] = 2
                            map["subType"] = 4//题卷本
                            map["grade"] = book.grade
                            map["bookId"] = book.bookId
                            map["bgResId"] = book.imageUrl
                            mPresenter.addType(map)
                        } else {
                            showToast(1,"已设置")
                        }
                    }
                }
            }
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun fetchData() {
        books = TextbookGreenDaoManager.getInstance().queryAllTextBook(typeId, pageIndex, pageSize)
        val total = TextbookGreenDaoManager.getInstance().queryAllTextBook(typeId)
        setPageNumber(total.size)
        mAdapter?.setNewData(books)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            AUTO_REFRESH_EVENT->{
                mQiniuPresenter.getToken()
            }
            TEXT_BOOK_EVENT->{
                fetchData()
            }
        }
    }

    override fun onUpload(token: String){
        cloudList.clear()
        val books = TextbookGreenDaoManager.getInstance().queryTextBookByHalfYear()
        for (book in books) {
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)) {
                FileUploadManager(token).apply {
                    startUpload(book.bookDrawPath, book.bookId.toString())
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 2
                            zipUrl = book.downloadUrl
                            downloadUrl = it
                            subTypeStr = DataBeanManager.textbookType[book.category]
                            date = System.currentTimeMillis()
                            listJson = Gson().toJson(book)
                            bookId = book.bookId
                            bookTypeId = book.category
                        })
                        if (cloudList.size == books.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                cloudList.add(CloudListBean().apply {
                    type = 2
                    zipUrl = book.downloadUrl
                    subTypeStr = DataBeanManager.textbookType[book.category]
                    date = System.currentTimeMillis()
                    listJson = Gson().toJson(book)
                    bookId = book.bookId
                    bookTypeId = book.category
                })
                if (cloudList.size == books.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList) {
            val bookBean = TextbookGreenDaoManager.getInstance().queryTextBookByBookId(item.bookTypeId, item.bookId)
            MethodManager.deleteTextbook(bookBean)
        }
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

}