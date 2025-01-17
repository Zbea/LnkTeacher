package com.bll.lnkteacher.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.AppUpdateDialog
import com.bll.lnkteacher.dialog.ProgressDialog
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.presenter.CommonPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.ExceptionHandle
import com.bll.lnkteacher.net.IBaseView
import com.bll.lnkteacher.ui.activity.CloudStorageActivity
import com.bll.lnkteacher.ui.activity.MainActivity
import com.bll.lnkteacher.ui.activity.ResourceCenterActivity
import com.bll.lnkteacher.ui.adapter.TabTypeAdapter
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.KeyboardUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SToast
import com.bll.lnkteacher.widget.FlowLayoutManager
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.common_fragment_title.tv_search
import kotlinx.android.synthetic.main.common_fragment_title.tv_title
import kotlinx.android.synthetic.main.common_page_number.btn_page_down
import kotlinx.android.synthetic.main.common_page_number.btn_page_up
import kotlinx.android.synthetic.main.common_page_number.ll_page_number
import kotlinx.android.synthetic.main.common_page_number.tv_page_current
import kotlinx.android.synthetic.main.common_page_number.tv_page_total_bottom
import kotlinx.android.synthetic.main.fragment_list.rv_tab
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
    var mUser:User?=null

    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据
    var cloudList= mutableListOf<CloudListBean>()
    var updateDialog: AppUpdateDialog?=null
    var mTabTypeAdapter: TabTypeAdapter?=null
    var itemTabTypes= mutableListOf<ItemTypeBean>()
    var grade=0

    override fun onCommon(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
            DataBeanManager.grades=commonData.grade
        if (!commonData.subject.isNullOrEmpty())
            DataBeanManager.courses=commonData.subject
        if (!commonData.typeGrade.isNullOrEmpty())
            DataBeanManager.typeGrades=commonData.typeGrade
        if (!commonData.version.isNullOrEmpty())
            DataBeanManager.versions=commonData.version
    }

    override fun onListSchools(list: MutableList<SchoolBean>) {
        DataBeanManager.schools=list
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

        mUser=MethodManager.getUser()

        initCommonTitle()
        if (rv_tab!=null){
            initTabView()
        }
        initView()
        initDialog()
        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
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

    private fun initDialog(){
        mDialog = ProgressDialog(requireActivity(),getScreenPosition())
    }

    protected fun initDialog(screen:Int){
        mDialog = ProgressDialog(requireActivity(),screen)
    }

    open fun initChangeScreenData(){
        initDialog()
    }

    /**
     * 获取当前屏幕位置
     */
    protected fun getScreenPosition():Int{
        var screenPos=0
        if (activity is MainActivity){
            screenPos=(activity as MainActivity).getCurrentScreenPos()
        }
        if (activity is CloudStorageActivity){
            screenPos=(activity as CloudStorageActivity).getCurrentScreenPos()
        }
        if (activity is ResourceCenterActivity){
            screenPos=(activity as ResourceCenterActivity).getCurrentScreenPos()
        }
        return screenPos
    }

    protected fun setTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    protected fun setTitle(titleResId: Int) {
        tv_title?.text = getString(titleResId)
    }

    protected fun showSearch(isShow:Boolean) {
        if (isShow){
            showView(tv_search)
        }
        else{
            disMissView(tv_search)
        }
    }

    protected fun setImageManager(setId:Int){
        showView(iv_manager)
        iv_manager?.setImageResource(setId)
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

    private fun initTabView(){
        rv_tab.layoutManager = FlowLayoutManager()//创建布局管理
        mTabTypeAdapter = TabTypeAdapter(R.layout.item_tab_type, null).apply {
            rv_tab.adapter = this
            bindToRecyclerView(rv_tab)
            setOnItemClickListener { adapter, view, position ->
                for (item in data){
                    item.isCheck=false
                }
                if (position<data.size){
                    val item=data[position]
                    item.isCheck=true
                    mTabTypeAdapter?.notifyDataSetChanged()
                    onTabClickListener(view,position)
                }
            }
        }
    }

    /**
     * tab点击监听
     */
    open fun onTabClickListener(view:View, position:Int){

    }

    /**
     * 关闭软键盘
     */
    fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(activity)
    }

    fun showToast(s:String){
        SToast.showText(getScreenPosition(),s)
    }

    fun showToast(strId:Int){
        SToast.showText(getScreenPosition(),strId)
    }

    fun showToast(screen: Int,s:String){
        SToast.showText(screen,s)
    }

    fun showToast(screen: Int,strId:Int){
        SToast.showText(screen,strId)
    }

    fun showLog(s:String){
        Log.d("debug",s)
    }

    fun showLog(resId:Int){
        Log.d("debug",getString(resId))
    }

    /**
     * 跳转活动(关闭已经打开的)
     */
    fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
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
                tv_page_total_bottom.text = pageCount.toString()
                showView(ll_page_number)
            }
        }
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        if (mView==null||activity==null)return
        showToast(R.string.login_timeout)
        MethodManager.logout(requireActivity())
    }

    override fun hideLoading() {
        if (mView==null||activity==null)return
        mDialog?.dismiss()
    }
    override fun showLoading() {
        mDialog?.show()
    }
    override fun fail(screen: Int, msg: String) {
        if (mView==null||activity==null)return
        showToast(screen,msg)
    }
    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog(R.string.connect_server_timeout)
    }
    override fun onComplete() {
        showLog(R.string.request_success)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden&&NetworkUtil(requireActivity()).isNetworkConnected()){
            onRefreshData()
        }
    }

    fun fetchCommonData(){
        if (NetworkUtil(requireActivity()).isNetworkConnected()){
            mCommonPresenter.getCommon()
            mCommonPresenter.getSchool()
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                lazyLoad()
            }
            else->{
                onEventBusMessage(msgFlag)
            }
        }
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
     * 重新初始化屏幕位置
     */
    open fun changeInitData(){
        initDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
