package com.bll.lnkteacher.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.AnalyseUserDetailsDialog
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.dialog.ProgressDialog
import com.bll.lnkteacher.mvp.model.AppUpdateBean
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.presenter.CommonPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.ExceptionHandle
import com.bll.lnkteacher.net.IBaseView
import com.bll.lnkteacher.ui.adapter.ClassAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseMultiAdapter
import com.bll.lnkteacher.ui.adapter.TabTypeAdapter
import com.bll.lnkteacher.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicScoreAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.FlowLayoutManager
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_list_tab.*
import kotlinx.android.synthetic.main.ac_testpaper_analyse.barChart
import kotlinx.android.synthetic.main.ac_testpaper_analyse.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse_teaching.*
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_answer.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.ceil


abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView, IContractView.ICommonView {

    private var mCommonPresenter= CommonPresenter(this)
    var screenPos=0
    var mDialog: ProgressDialog? = null
    var mSaveState:Bundle?=null
    var mUser=SPUtil.getObj("user",User::class.java)
    var mUserId=SPUtil.getObj("user",User::class.java)?.accountId
    var pageIndex=1 //当前页码
    var pageCount=1 //全部数据
    var pageSize=0 //一页数据
    var isClickExpand=false //是否是单双屏切换
    var mTabTypeAdapter: TabTypeAdapter?=null
    var itemTabTypes= mutableListOf<ItemTypeBean>()

    var mExamClassGroups= mutableListOf<ClassGroup>()
    var mClassAdapter:ClassAdapter?=null

    var mTopicScoreAdapter: TopicScoreAdapter?=null
    var mTopicMultiAdapter: TopicMultiScoreAdapter?=null

    var mAnalyseAdapter: ExamAnalyseAdapter?=null
    var mAnalyseMultiAdapter: ExamAnalyseMultiAdapter?=null
    var totalAnalyseItems = mutableListOf<AnalyseItem>() //题目集合

    var currentScores=mutableListOf<ScoreItem>()//初始题目分数
    var correctModule=-1//批改摸排
    var scoreMode = 0//1打分
    var correctStatus=0//批改状态
    var answerImages= mutableListOf<String>()
    var answerPos=0

    open fun navigationToFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_layout, fragment, (fragment as Any?)!!.javaClass.simpleName)
                .addToBackStack(null).commitAllowingStateLoss()
        }
    }

    open fun popToStack(fragment: Fragment?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        fragmentManager.popBackStack()
    }

    override fun onCommon(commonData: CommonData) {
        if (!commonData.grade.isNullOrEmpty())
            DataBeanManager.grades=commonData.grade
        if (!commonData.subject.isNullOrEmpty())
            DataBeanManager.courses=commonData.subject
        if (!commonData.typeGrade.isNullOrEmpty())
            DataBeanManager.typeGrades=commonData.typeGrade
        if (!commonData.version.isNullOrEmpty())
            DataBeanManager.versions=commonData.version

        onCommonData()
    }

    override fun onAppUpdate(item: AppUpdateBean?) {
    }

    override fun onClassList(classGroups: MutableList<ClassGroup>?) {
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSaveState=savedInstanceState
        setContentView(layoutId())
        initCommonTitle()
        EventBus.getDefault().register(this)
        setStatusBarColor(ContextCompat.getColor(this, R.color.white))

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )){
            EasyPermissions.requestPermissions(this,"请求权限",1,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )
        }

        screenPos=getCurrentScreenPos()
        if (rv_tab!=null){
            initTabView()
        }

        if (rv_class!=null){
            initRecyclerViewClass()
        }

        initCreate()
        initDialog()
        initData()
        initView()
        fetchCommonData()
    }

    /**
     * 初始化onCreate
     */
    open fun initCreate(){
    }

    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    @SuppressLint("WrongViewCast")
    fun initCommonTitle() {

        iv_back?.setOnClickListener { finish() }

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
        mDialog = ProgressDialog(this,getCurrentScreenPos())
    }

    protected fun initDialog(screen:Int){
        mDialog = ProgressDialog(this,screen)
    }

    protected fun fetchCommonData(){
        if (NetworkUtil(this).isNetworkConnected() && DataBeanManager.grades.size==0)
            mCommonPresenter.getCommon()
    }

    fun showBackView(isShow:Boolean) {
        if (isShow){
            showView(iv_back)
        }
        else{
            disMissView(iv_back)
        }
    }

    protected fun setImageSetting(setId:Int){
        showView(iv_setting)
        iv_setting?.setImageResource(setId)
    }

    protected fun setImageManager(setId:Int){
        showView(iv_manager)
        iv_manager?.setImageResource(setId)
    }

    protected fun setPageTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    protected fun setPageTitle(titleId: Int) {
        tv_title?.setText(titleId)
    }

    protected fun setPageOk(str: String){
        showView(tv_btn_1)
        tv_btn_1.text=str
    }

    protected fun setPageSetting(str: String){
        showView(tv_custom_1)
        tv_custom_1.text=str
    }

    protected fun setPageCustom(str: String){
        showView(tv_custom)
        tv_custom.text=str
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
     * 消失view
     */
    protected fun disInvisbleView(vararg views: View?) {
        for (view in views) {
            if (view != null && view.visibility != View.GONE) {
                view.visibility = View.INVISIBLE
            }
        }
    }

    private fun initTabView(){
        rv_tab.layoutManager = FlowLayoutManager()//创建布局管理
        mTabTypeAdapter = TabTypeAdapter(R.layout.item_tab_type, null).apply {
            rv_tab.adapter = this
            bindToRecyclerView(rv_tab)
            setOnItemClickListener { adapter, view, position ->
                for (item in mTabTypeAdapter?.data!!){
                    item.isCheck=false
                }
                val item=mTabTypeAdapter?.data!![position]
                item.isCheck=true
                mTabTypeAdapter?.notifyDataSetChanged()

                onTabClickListener(view,position)
            }
        }
    }


    /**
     * tab点击监听
     */
    open fun onTabClickListener(view:View, position:Int){

    }

    private fun initRecyclerViewClass(){
        rv_class.layoutManager = FlowLayoutManager()
        mClassAdapter = ClassAdapter(R.layout.item_class, null)
        rv_class.adapter = mClassAdapter
        mClassAdapter?.bindToRecyclerView(rv_class)
        mClassAdapter?.setOnItemClickListener { adapter, view, position ->
            for (item in mExamClassGroups){
                item.isCheck=false
            }
            val item=mExamClassGroups[position]
            item.isCheck=true
            mClassAdapter?.notifyDataSetChanged()

            onClassClickListener(view,position)
        }
    }

    /**
     * clas点击监听
     */
    open fun onClassClickListener(view:View, position:Int){

    }

    /**
     * 小题得分数据展示初始化
     */
    fun initRecyclerViewScore(){
        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0, DP2PX.dip2px(this,100f))
        }

        if (correctModule<3){
            rv_list_score.layoutManager = GridLayoutManager(this,2)
            mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_score,scoreMode,correctModule,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setOnItemChildClickListener { adapter, view, position ->
                    if (correctStatus==1){
                        val item=currentScores[position]
                        when(view.id){
                            R.id.tv_score->{
                                if (scoreMode==1){
                                    NumberDialog(this@BaseActivity,item.label).builder().setDialogClickListener{
                                        if (item.label!=it){
                                            item.result=0
                                        }
                                        item.score= it.toString()
                                        notifyItemChanged(position)
                                        setTotalScore()
                                    }
                                }
                            }
                            R.id.iv_result->{
                                if (item.result==0){
                                    item.result=1
                                }
                                else{
                                    item.result=0
                                }
                                if (scoreMode==1){
                                    item.score= (item.result*item.label).toString()
                                }
                                else{
                                    item.score=item.result.toString()
                                }
                                notifyItemChanged(position)
                                setTotalScore()
                            }
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceGridItemDeco(2,DP2PX.dip2px(this@BaseActivity,15f)))
            }
        }
        else{
            rv_list_score.layoutManager = LinearLayoutManager(this)
            mTopicMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,scoreMode,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setCustomItemChildClickListener{ position, view, childPos ->
                    if (correctStatus==1){
                        val scoreItem=currentScores[position]
                        val childItem=scoreItem.childScores[childPos]
                        when(view.id){
                            R.id.tv_score->{
                                if (scoreMode==1){
                                    NumberDialog(this@BaseActivity,childItem.label).builder().setDialogClickListener{
                                        if (childItem.label!=it){
                                            childItem.result=0
                                        }
                                        childItem.score= it.toString()
                                        //获取小题总分
                                        var scoreTotal=0
                                        for (item in scoreItem.childScores){
                                            scoreTotal+=MethodManager.getScore(item.score)
                                        }
                                        scoreItem.score=scoreTotal.toString()
                                        notifyItemChanged(position)
                                        setTotalScore()
                                    }
                                }
                            }
                            R.id.iv_result->{
                                if (childItem.result==0){
                                    childItem.result=1
                                }
                                else{
                                    childItem.result=0
                                }
                                if (scoreMode==1){
                                    childItem.score= (childItem.result*childItem.label).toString()
                                    //获取小题总分
                                    var scoreTotal=0
                                    for (item in scoreItem.childScores){
                                        scoreTotal+=MethodManager.getScore(item.score)
                                    }
                                    scoreItem.score=scoreTotal.toString()
                                }
                                else{
                                    var totalRight=0
                                    for (item in scoreItem.childScores){
                                        if (item.result==1)
                                            totalRight+= 1
                                    }
                                    scoreItem.score=totalRight.toString()
                                }
                                notifyItemChanged(position)
                                setTotalScore()
                            }
                        }
                    }
                }
                rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@BaseActivity,15f)))
            }
        }
    }

    /**
     * 总分变化
     */
    private fun setTotalScore(){
        if (tv_total_score!=null){
            var total=0
            for (item in currentScores){
                total+=MethodManager.getScore(item.score)
            }
            tv_total_score.text=total.toString()
        }
    }

    /**
     * 设置答案view
     */
    fun setAnswerView(){
        tv_answer.setOnClickListener {
            showView(rl_answer)
        }
        iv_close_answer.setOnClickListener {
            disMissView(rl_answer)
        }

        btn_answer_down.setOnClickListener {
            if (answerPos< answerImages.size-1){
                answerPos+=1
                GlideUtils.setImageUrl(this,answerImages[answerPos],iv_answer)
                setAnswerPageView()
            }
        }

        btn_answer_up.setOnClickListener {
            if (answerPos>0){
                answerPos-=1
                GlideUtils.setImageUrl(this,answerImages[answerPos],iv_answer)
                setAnswerPageView()
            }
        }

        GlideUtils.setImageUrl(this,answerImages[answerPos],iv_answer)
        setAnswerPageView()
    }

    private fun setAnswerPageView(){
        tv_answer_total?.text="${answerImages.size}"
        tv_answer_current?.text="${answerPos+1}"
    }

    /**
     * 数据分析初始化
     */
    fun initRecyclerAnalyse() {
        if (correctModule < 3) {
            rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
            mAnalyseAdapter = ExamAnalyseAdapter(R.layout.item_exam_analyse_score, correctModule, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setOnItemChildClickListener { adapter, view, position ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].wrongStudents
                        if (students.size>0)
                            AnalyseUserDetailsDialog(this@BaseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(this, 15f)))
        } else {
            rv_list.layoutManager = LinearLayoutManager(this)
            mAnalyseMultiAdapter = ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setCustomItemChildClickListener { position, view, childPosition ->
                    if (view.id == R.id.tv_wrong_num) {
                        val students = totalAnalyseItems[position].childAnalyses[childPosition].wrongStudents
                        if (students.size>0)
                            AnalyseUserDetailsDialog(this@BaseActivity, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this, 15f)))
        }
    }

    /**
     * 初始化柱状图
     */
    fun initChartView() {
        barChart.description.isEnabled=false
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.setTouchEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setMaxVisibleValueCount(30)

        //x轴设置
        val xAxis=barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.axisLineColor= Color.parseColor("#000000")
        xAxis.textColor= Color.parseColor("#000000")
        xAxis.textSize=18f

        // Y轴左边
        val  leftAxis = barChart.axisLeft
        leftAxis.setAxisMinValue(0f) // 设置最小值
        leftAxis.setDrawGridLines(false)
        leftAxis.axisLineColor= Color.parseColor("#000000")
        leftAxis.textSize=14f
        //右侧Y轴自定义值
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return  ToolUtils.getFormatNum(value*100,"#")+"%"
            }
        }

        //y轴右边设置
        val rightAxis = barChart.axisRight
        rightAxis.setAxisMinValue(0f) // 设置最小值
        rightAxis.setDrawGridLines(false) // 不绘制网格线
        rightAxis.isEnabled=false
    }

    /**
     * 设置柱状图
     */
    fun setChartView(){
        if (correctModule > 0) {
            val barEntries= mutableListOf<BarEntry>()
            val topicStrs= mutableListOf<String>()
            if (correctModule < 3) {
                for (i in 0 until totalAnalyseItems.size){
                    topicStrs.add("${i+1}")
                    barEntries.add(BarEntry(i.toFloat(), totalAnalyseItems[i].scoreRate.toFloat()))
                }
            } else {
                var count=0
                for (item in totalAnalyseItems){
                    val childItems=item.childAnalyses
                    for (i in 0 until childItems.size){
                        topicStrs.add("${count+i+1}")
                        barEntries.add(BarEntry((count+i).toFloat(), childItems[i].scoreRate.toFloat()))
                    }
                    count+=childItems.size
                }
            }

            if (barEntries.size<30){
                for (i in barEntries.size until 30){
                    topicStrs.add("")
                    barEntries.add(BarEntry(i.toFloat(), 0f))
                }
            }

            val barDataSet= BarDataSet(barEntries,"")
            barDataSet.barBorderWidth=0.5f
            barDataSet.barBorderColor=Color.BLACK
            val barData= BarData(barDataSet)

            val xAxis=barChart.xAxis
            xAxis.valueFormatter= IndexAxisValueFormatter(topicStrs)
            xAxis.labelCount=topicStrs.size

            barChart.data=barData
            barChart.setFitBars(false)
            barChart.invalidate()
        }

    }

    /**
     * 格式序列化 题目分数集合转成字符串
     */
    fun toJson(list:List<ScoreItem>):String{
        return if (correctModule<3){
            Gson().toJson(list)
        } else{
            val items= arrayListOf <List<ScoreItem>>()
            for (item in list){
                items.add(item.childScores)
            }
            Gson().toJson(items)
        }
    }

    /**
     * 格式序列化  题目分数转行list集合
     */
    fun jsonToList(json:String):List<ScoreItem>{
        var items= mutableListOf<ScoreItem>()
        if (correctModule<3){
            items= Gson().fromJson(json, object : TypeToken<List<ScoreItem>>() {}.type) as MutableList<ScoreItem>
            for (item in items){
                item.sort=items.indexOf(item)
            }
        }
        else{
            var totalChildSort=0//小题累加
            val scores= Gson().fromJson(json, object : TypeToken<List<List<ScoreItem>>>() {}.type) as MutableList<List<ScoreItem>>
            for (i in scores.indices){
                items.add(ScoreItem().apply {
                    sort=i
                    if (scoreMode==1){
                        //统计小题标准分
                        var totalLabel=0
                        for (item in scores[i]){
                            totalLabel+=item.label
                        }
                        label=totalLabel
                        //统计小题得分
                        var totalItem=0
                        for (item in scores[i]){
                            totalItem+= MethodManager.getScore(item.score)
                        }
                        score=totalItem.toString()
                        result=if (totalLabel==totalItem) 1 else 0
                    }
                    else{
                        //统计小题对数
                        var totalRight=0
                        for (item in scores[i]){
                            item.score=item.result.toString()
                            if (item.result==1) {
                                totalRight+= 1
                            }
                        }
                        label=scores.size
                        score=totalRight.toString()
                        result=if (scores.size==totalRight) 1 else 0
                    }
                    //小题重新排序
                    for (item in scores[i]){
                        //重置
                        if (correctModule==3){
                            item.sort=scores[i].indexOf(item)
                        }
                        else{
                            //小题累加
                            item.sort=totalChildSort+scores[i].indexOf(item)
                        }
                    }
                    childScores=scores[i]
                    totalChildSort+=scores[i].size
                })
            }
        }
        return items
    }


    /**
     * 设置翻页
     */
    protected fun setPageNumber(total:Int){
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

    /**
     * 得到当前屏幕位置
     */
    fun getCurrentScreenPos():Int{
        return getCurrentScreenPanel()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected fun setStatusBarColor(statusColor: Int) {
        val window = window
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏颜色
        window.statusBarColor = statusColor
        //设置系统状态栏处于可见状态
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //让view不根据系统窗口来调整自己的布局
        val mContentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val mChildView = mContentView.getChildAt(0)
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false)
            ViewCompat.requestApplyInsets(mChildView)
        }
    }

    /**
     * 关闭软键盘
     */
    protected fun hideKeyboard(){
        KeyboardUtils.hideSoftKeyboard(this)
    }

    protected fun showToast(s:String){
        SToast.showText(getCurrentScreenPos(),s)
    }

    protected fun showToast(resId:Int){
        SToast.showText(getCurrentScreenPos(),resId)
    }

    fun showToast(screen: Int,s:String){
        SToast.showText(screen,s)
    }

    fun showToast(screen: Int,sId:Int){
        SToast.showText(screen,sId)
    }

    protected fun showLog(s:String){
        Log.d("debug",s)
    }

    protected fun showLog(resId:Int){
        Log.d("debug",getString(resId))
    }

    /**
     * 跳转活动 已经打开过则关闭
     */
    protected fun customStartActivity(intent: Intent){
        ActivityManager.getInstance().finishActivity(intent.component?.className)
        startActivity(intent)
    }

    /**
     * 重写要申请权限的Activity或者Fragment的onRequestPermissionsResult()方法，
     * 在里面调用EasyPermissions.onRequestPermissionsResult()，实现回调。
     *
     * @param requestCode  权限请求的识别码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    /**
     * 当权限被成功申请的时候执行回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("EasyPermissions", "获取成功的权限$perms")
    }
    /**
     * 当权限申请失败的时候执行的回调
     *
     * @param requestCode 权限请求的识别码
     * @param perms       申请的权限的名字
     */
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        //处理权限名字字符串
        val sb = StringBuffer()
        for (str in perms) {
            sb.append(str)
            sb.append("\n")
        }
        sb.replace(sb.length - 2, sb.length, "")
        //用户点击拒绝并不在询问时候调用
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, "已拒绝权限" + sb + "并不再询问", Toast.LENGTH_SHORT).show()
            AppSettingsDialog.Builder(this)
                    .setRationale("此功能需要" + sb + "权限，否则无法正常使用，是否打开设置")
                    .setPositiveButton("好")
                    .setNegativeButton("不行")
                    .build()
                    .show()
        }
    }

    protected fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。")
        // 拒绝, 退出应用
        builder.setNegativeButton("取消") { dialog, which ->

        }
        builder.setPositiveButton("确定") { dialog, which -> startAppSettings() }

        builder.setCancelable(false)
        builder.show()
    }

    /**
     * 启动应用的设置
     */
    private fun startAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + "com.bll.lnkteacher")
        startActivity(intent)
    }

    override fun addSubscription(d: Disposable) {
    }
    override fun login() {
        SToast.showText(getCurrentScreenPos(),R.string.login_timeout)
        MethodManager.logout(this)
    }

    override fun hideLoading() {
        mDialog?.dismiss()
    }

    override fun showLoading() {
        mDialog!!.show()
    }

    override fun fail(screen: Int, msg: String) {
        showToast(screen,msg)
    }

    override fun onFailer(responeThrowable: ExceptionHandle.ResponeThrowable?) {
        showLog(R.string.connect_server_timeout)
    }
    override fun onComplete() {
        showLog(R.string.request_success)
    }

    override fun onPause() {
        super.onPause()
        mDialog!!.dismiss()
    }

    open fun fetchData(){

    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when(msgFlag){
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT->{
                onRefreshData()
            }
            else->{
                onEventBusMessage(msgFlag)
            }
        }
    }

    /**
     * 刷新数据
     */
    open fun onRefreshData(){
    }

    /**
     * 收到eventbus事件处理
     */
    open fun onEventBusMessage(msgFlag: String){
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!isClickExpand){
            screenPos=getCurrentScreenPos()
        }
        initDialog()
        initChangeScreenData()
        isClickExpand=false
    }

    /**
     * 切屏后，重新初始化数据（用于数据请求弹框显示正确的位置）
     */
    open fun initChangeScreenData(){
    }

    open fun onCommonData(){

    }


}


