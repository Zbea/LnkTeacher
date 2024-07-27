package com.bll.lnkteacher.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.presenter.HandoutPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.fragment.textbook.HandoutFragment
import com.bll.lnkteacher.ui.fragment.textbook.TextbookFragment
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SPUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class TextbookManagerFragment : BaseMainFragment(),IContractView.IHandoutView{
    private val presenter=HandoutPresenter(this,1)
    private var textFragments= mutableListOf<TextbookFragment>()
    private var handoutFragment: HandoutFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private val handoutsTypes= mutableListOf<PopupBean>()
    private var types= mutableListOf<String>()

    override fun onType(list: MutableList<String>) {
        if (list.size>0){
            if (types!=list){
                SPUtil.putListString("handoutTypes",list)
                handoutsTypes.clear()
                for (i in list.indices){
                    handoutsTypes.add(PopupBean(i,list[i],i==0))
                }
                types=list
                tv_grade.text=list[0]
                handoutFragment?.changeType(list[0])
            }
        }
        else{
            SPUtil.putListString("handoutTypes", mutableListOf())
            disMissView(tv_grade)
        }
    }
    override fun onList(list: HandoutList?) {
    }
    override fun onSuccess() {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook_manager
    }

    override fun initView() {
        super.initView()
        setTitle(DataBeanManager.getIndexLeftData()[2].name)
        types=SPUtil.getListStrings("handoutTypes")
        for (i in types.indices){
            handoutsTypes.add(PopupBean(i,types[i],i==0))
        }

        val list=DataBeanManager.textbookType.toMutableList()
        list.removeLast()

        for (textbook in list){
            textFragments.add(TextbookFragment().newInstance(textbook))
        }
        handoutFragment = HandoutFragment()
        switchFragment(lastFragment, textFragments[0])

        if (types.size>0){
            tv_grade.text=types[0]
            handoutFragment?.changeType(types[0])
        }

        tv_grade.setOnClickListener {
            PopupRadioList(requireActivity(),handoutsTypes,tv_grade,0).builder().setOnSelectListener{
                handoutFragment?.changeType(it.name)
            }
        }

        initTab()
    }

    override fun lazyLoad() {
        presenter.getTypeList()
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
    }

    override fun onTabClickListener(view: View, position: Int) {
        when (position) {
            5 -> {
                if (handoutsTypes.size>0)
                    showView(tv_grade)
                switchFragment(lastFragment, handoutFragment)
            }
            else -> {
                disMissView(tv_grade)
                switchFragment(lastFragment, textFragments[position])
            }
        }
        lastPosition = position
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager
            val ft = fm?.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.add(R.id.fl_content_textbook, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    override fun onRefreshData() {
        lazyLoad()
    }


    /**
     * 每天上传书籍
     */
    fun upload(tokenStr: String) {
        cloudList.clear()
        val maxBooks = mutableListOf<Book>()
        val books = BookGreenDaoManager.getInstance().queryAllTextBook()
        //遍历获取所有需要上传的书籍数目
        for (item in books) {
            if (System.currentTimeMillis() >= item.time + Constants.halfYear) {
                maxBooks.add(item)
            }
        }
        for (book in maxBooks) {
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)) {
                FileUploadManager(tokenStr).apply {
                    startUpload(book.bookDrawPath, book.bookId.toString())
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 2
                            zipUrl = book.downloadUrl
                            downloadUrl = it
                            subTypeStr = book.subtypeStr
                            date = System.currentTimeMillis()
                            listJson = Gson().toJson(book)
                            bookId = book.bookId
                            bookTypeId = book.typeId
                        })
                        if (cloudList.size == maxBooks.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                cloudList.add(CloudListBean().apply {
                    type = 2
                    zipUrl = book.downloadUrl
                    subTypeStr = book.subtypeStr
                    date = System.currentTimeMillis()
                    listJson = Gson().toJson(book)
                    bookId = book.bookId
                    bookTypeId = book.typeId
                })
                if (cloudList.size == maxBooks.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    //上传完成后删除书籍
    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        for (item in cloudList) {
            val bookBean = BookGreenDaoManager.getInstance().queryTextBookByBookID(item.bookTypeId, item.bookId)
            //删除书籍
            FileUtils.deleteFile(File(bookBean.bookPath))
            FileUtils.deleteFile(File(bookBean.bookDrawPath))
            BookGreenDaoManager.getInstance().deleteBook(bookBean)
        }
        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT)
    }

}