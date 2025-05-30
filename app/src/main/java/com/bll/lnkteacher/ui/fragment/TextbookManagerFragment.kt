package com.bll.lnkteacher.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.Constants.Companion.SP_HANDOUT_TYPES
import com.bll.lnkteacher.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.manager.TextbookGreenDaoManager
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.HandoutPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.fragment.textbook.HandoutFragment
import com.bll.lnkteacher.ui.fragment.textbook.TextbookFragment
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade
import org.greenrobot.eventbus.EventBus

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
                SPUtil.putListString(SP_HANDOUT_TYPES,list)
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
            SPUtil.putListString(SP_HANDOUT_TYPES, mutableListOf())
            disMissView(tv_grade)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook_manager
    }

    override fun initView() {
        super.initView()
        setTitle(DataBeanManager.getIndexLeftData()[2].name)
        types=SPUtil.getListStrings(SP_HANDOUT_TYPES)
        for (i in types.indices){
            handoutsTypes.add(PopupBean(i,types[i],i==0))
        }

        for (item in DataBeanManager.getTextbookFragment()){
            textFragments.add(TextbookFragment().newInstance(item.id))
        }
        handoutFragment = HandoutFragment()
        switchFragment(lastFragment, textFragments[0])

        if (types.size>0){
            tv_grade.text=types[0]
            handoutFragment?.changeType(types[0])
        }

        tv_grade.setOnClickListener {
            PopupRadioList(requireActivity(),handoutsTypes,tv_grade,5).builder().setOnSelectListener{
                handoutFragment?.changeType(it.name)
            }
        }

        initTab()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkConnected())
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
            4 -> {
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

    override fun onEventBusMessage(msgFlag: String) {
        when(msgFlag){
            Constants.AUTO_REFRESH_EVENT->{
                mQiniuPresenter.getToken()
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