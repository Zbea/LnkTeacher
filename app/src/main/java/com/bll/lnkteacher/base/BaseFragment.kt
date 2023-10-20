package com.bll.lnkteacher.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.ProgressDialog
import com.bll.lnkteacher.manager.NoteDaoManager
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.Note
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.presenter.CommonPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.ExceptionHandle
import com.bll.lnkteacher.net.IBaseView
import com.bll.lnkteacher.ui.activity.NoteDrawingActivity
import com.bll.lnkteacher.utils.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_bookstore.*
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_page_number.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil


abstract class BaseFragment : Fragment(), IBaseView,  IContractView.ICommonView{

    var mCommonPresenter= CommonPresenter(this)

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    var mView:View?=null
    var mDialog: ProgressDialog? = null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据
    var privacyPassword:PrivacyPassword?=null

    override fun onClassList(groups: MutableList<ClassGroup>) {
        DataBeanManager.classGroups=groups
        onClassGroupEvent()
    }

    override fun onGroupList(groups: MutableList<Group>) {
        val schools= mutableListOf<Group>()
        for (item in groups){
            schools.add(item)
        }
        DataBeanManager.schoolGroups=schools
        onGroupEvent()
    }

    override fun onCommon(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
        {
            DataBeanManager.grades=commonData.grade
        }
        if (!commonData.subject.isNullOrEmpty())
            DataBeanManager.courses=commonData.subject
        if (!commonData.typeGrade.isNullOrEmpty())
            DataBeanManager.typeGrades=commonData.typeGrade
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null != mView) {
            val parent: ViewGroup? = container
            parent?.removeView(parent)
        } else {
            mView = inflater.inflate(getLayoutId(), container,false)
        }

        return mView
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        isViewPrepare = true
        privacyPassword=getPrivacyPasswordObj()
        initCommonTitle()
        initView()
        mDialog = ProgressDialog(activity)
        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(activity)
    }

    fun showToast(s:String){
        SToast.showText(s)
    }

    fun showToast(strId:Int){
        SToast.showText(strId)
    }

    fun showLog(s:String){
        Log.d("debug",s)
    }

    fun showLog(resId:Int){
        Log.d("debug",getString(resId))
    }

    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    @SuppressLint("WrongViewCast")
    fun initCommonTitle() {
        btn_page_up?.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                fetchData()
            }
        }

        btn_page_down?.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }

    }


    fun setTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    fun setTitle(titleResId: Int) {
        tv_title?.text = getString(titleResId)
    }

    fun showSearch(isShow:Boolean) {
        if (isShow){
            showView(tv_search)
        }
        else{
            disMissView(tv_search)
        }
    }

    /**
     * 显示view
     */
    protected fun showView(view: View?) {
        if (view != null && view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }

    /**
     * 显示view
     */
    protected fun showView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
            }
        }
    }


    /**
     * 消失view
     */
    protected fun disMissView(view: View?) {
        if (view != null && view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    /**
     * 消失view
     */
    protected fun disMissView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.GONE) {
                view.visibility = View.GONE
            }
        }
    }

    /**
     * 获取查看密码
     */
    fun getPrivacyPasswordObj(): PrivacyPassword? {
        return SPUtil.getObj("${mUser?.accountId}PrivacyPassword",
            PrivacyPassword::class.java)
    }

    /**
     * 设置翻页
     */
    fun setPageNumber(total:Int){
        if (ll_page_number!=null){
            pageCount = ceil(total.toDouble() / pageSize).toInt()
            if (total == 0) {
                disMissView(ll_page_number)
            } else {
                tv_page_current.text = pageIndex.toString()
                tv_page_total.text = pageCount.toString()
                showView(ll_page_number)
            }
        }
    }

    fun getRadioButton(i:Int,str:String,max:Int): RadioButton {
        val radioButton =
            layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
        radioButton.text = str
        radioButton.id = i
        radioButton.isChecked = i == 0
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            DP2PX.dip2px(activity, 45f))

        layoutParams.marginEnd = if (i == max) 0 else DP2PX.dip2px(activity, 44f)
        radioButton.layoutParams = layoutParams

        return radioButton
    }

    fun getRadioButton(i:Int,check:Int,str:String,max:Int):RadioButton{
        val radioButton =
            layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
        radioButton.text = str
        radioButton.id = i
        radioButton.isChecked = i == check
        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            DP2PX.dip2px(activity, 45f))

        layoutParams.marginEnd = if (i == max) 0 else DP2PX.dip2px(activity, 44f)
        radioButton.layoutParams = layoutParams

        return radioButton
    }

    /**
     * 跳转笔记写作
     */
    fun gotoNote(note: Note) {
        note.date=System.currentTimeMillis()
        NoteDaoManager.getInstance().insertOrReplace(note)
        EventBus.getDefault().post(Constants.NOTE_EVENT)

        val intent = Intent(activity, NoteDrawingActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("noteBundle",note)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        if (mView==null||activity==null)return
        SToast.showText(R.string.login_timeout)
        MethodManager.logout(requireActivity())
    }

    override fun hideLoading() {
        if (mView==null||activity==null)return
        mDialog?.dismiss()
    }
    override fun showLoading() {
        mDialog?.show()
    }
    override fun fail(msg: String) {
        if (mView==null||activity==null)return
        SToast.showText( msg)
    }
    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog(R.string.connect_server_timeout)
    }
    override fun onComplete() {
        showLog(R.string.request_success)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden&&NetworkUtil.isNetworkAvailable(requireActivity())){
            onRefreshData()
        }
    }

    fun fetchCommonData(){
        mCommonPresenter.getClassGroups()
        mCommonPresenter.getGroups()
        if (DataBeanManager.grades.size==0)
            mCommonPresenter.getCommon()
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
//        when(msgFlag){
//            Constants.USER_EVENT->{
//                mUser= SPUtil.getObj("user", User::class.java)
//            }
//            else->{
//                onEventBusMessage(msgFlag)
//            }
//        }
        onEventBusMessage(msgFlag)
    }

    /**
     * 收到eventbus事件处理
     */
    open fun onEventBusMessage(msgFlag: String){
    }

    /**
     * 每次翻页，刷新数据
     */
    open fun onRefreshData(){
    }

    /**
     * 网络请求数据
     */
    open fun fetchData(){
    }

    /**
     * 班级事件
     */
    open fun onClassGroupEvent(){
    }

    /**
     * 群事件
     */
    open fun onGroupEvent(){
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
