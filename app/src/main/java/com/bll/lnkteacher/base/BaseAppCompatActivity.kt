package com.bll.lnkteacher.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.AnalyseUserDetailsDialog
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.dialog.ProgressDialog
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem
import com.bll.lnkteacher.mvp.model.testpaper.ScoreItem
import com.bll.lnkteacher.mvp.presenter.CommonPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.ExceptionHandle
import com.bll.lnkteacher.net.IBaseView
import com.bll.lnkteacher.ui.adapter.ExamAnalyseAdapter
import com.bll.lnkteacher.ui.adapter.ExamAnalyseMultiAdapter
import com.bll.lnkteacher.ui.adapter.TabTypeAdapter
import com.bll.lnkteacher.ui.adapter.TopicMultistageScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicTwoScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicTwoScoreAdapter.ChildAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.FlowLayoutManager
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ac_list_tab.*
import kotlinx.android.synthetic.main.ac_testpaper_analyse.barChart
import kotlinx.android.synthetic.main.ac_testpaper_analyse.iv_analyse_down
import kotlinx.android.synthetic.main.ac_testpaper_analyse.iv_analyse_up
import kotlinx.android.synthetic.main.ac_testpaper_analyse.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse_teaching.*
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.ceil


abstract class BaseAppCompatActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, IBaseView, IContractView.ICommonView {

    var mCommonPresenter = CommonPresenter(this)
    var screenPos = 0
    var mDialog: ProgressDialog? = null
    var mSaveState: Bundle? = null
    var mUser: User? = null
    var mUserId = 0L
    var pageIndex = 1 //当前页码
    var pageCount = 1 //全部数据
    var pageSize = 0 //一页数据
    var isClickExpand = false //是否是单双屏切换
    var mTabTypeAdapter: TabTypeAdapter? = null
    var itemTabTypes = mutableListOf<ItemTypeBean>()

    var mTopicScoreAdapter: TopicScoreAdapter? = null
    var mTopicTwoScoreAdapter: TopicTwoScoreAdapter? = null
    var mTopicMultistageScoreAdapter: TopicMultistageScoreAdapter? = null

    var mAnalyseAdapter: ExamAnalyseAdapter? = null
    var mAnalyseMultiAdapter: ExamAnalyseMultiAdapter? = null
    var totalAnalyseItems = mutableListOf<AnalyseItem>() //题目集合

    var currentScores = mutableListOf<ScoreItem>()//优化后题目分数
    var correctModule = -1//批改摸排
    var scoreMode = 0//1打分
    var correctStatus = 0//批改状态
    var answerImages = mutableListOf<String>()

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
            DataBeanManager.grades = commonData.grade
        if (!commonData.subject.isNullOrEmpty())
            DataBeanManager.courses = commonData.subject
        if (!commonData.typeGrade.isNullOrEmpty())
            DataBeanManager.typeGrades = commonData.typeGrade
        if (!commonData.version.isNullOrEmpty())
            DataBeanManager.versions = commonData.version

        onCommonData()
    }


    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSaveState = savedInstanceState
        setContentView(layoutId())
        initCommonTitle()
        EventBus.getDefault().register(this)
        setStatusBarColor(ContextCompat.getColor(this, R.color.white))

        if (!EasyPermissions.hasPermissions(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            EasyPermissions.requestPermissions(
                this, "请求权限", 1,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            )
        }

        mUser = MethodManager.getUser()
        mUserId=MethodManager.getAccountId()

        screenPos = getCurrentScreenPos()
        if (rv_tab != null) {
            initTabView()
        }

        initCreate()
        initDialog()
        initData()
        initView()
    }

    /**
     * 初始化onCreate
     */
    open fun initCreate() {
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
            if (pageIndex > 1) {
                pageIndex -= 1
                fetchData()
            }
        }

        btn_page_down?.setOnClickListener {
            if (pageIndex < pageCount) {
                pageIndex += 1
                fetchData()
            }
        }
    }

    private fun initDialog() {
        mDialog = ProgressDialog(this, getCurrentScreenPos())
    }

    protected fun initDialog(screen: Int) {
        mDialog = ProgressDialog(this, screen)
    }

    protected fun fetchCommonData() {
        if (NetworkUtil.isNetworkConnected()) {
            mCommonPresenter.getCommon()
        }
    }

    fun showBackView(isShow: Boolean) {
        if (isShow) {
            showView(iv_back)
        } else {
            disMissView(iv_back)
        }
    }

    protected fun setImageSetting(setId: Int) {
        showView(iv_setting)
        iv_setting?.setImageResource(setId)
    }

    protected fun setImageManager(setId: Int) {
        showView(iv_manager)
        iv_manager?.setImageResource(setId)
    }

    protected fun setPageTitle(pageTitle: String) {
        tv_title?.text = pageTitle
    }

    protected fun setPageTitle(titleId: Int) {
        tv_title?.setText(titleId)
    }

    protected fun setPageOk(str: String) {
        showView(tv_btn_1)
        tv_btn_1.text = str
    }

    protected fun setPageSetting(str: String) {
        showView(tv_custom_1)
        tv_custom_1.text = str
    }

    protected fun setPageCustom(str: String) {
        showView(tv_custom)
        tv_custom.text = str
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

    private fun initTabView() {
        rv_tab.layoutManager = FlowLayoutManager()//创建布局管理
        mTabTypeAdapter = TabTypeAdapter(R.layout.item_tab_type, null).apply {
            rv_tab.adapter = this
            bindToRecyclerView(rv_tab)
            setOnItemClickListener { adapter, view, position ->
                for (item in data) {
                    item.isCheck = false
                }
                if (position < data.size) {
                    val item = data[position]
                    item.isCheck = true
                    mTabTypeAdapter?.notifyDataSetChanged()
                    onTabClickListener(view, position)
                }
            }
        }
    }


    /**
     * tab点击监听
     */
    open fun onTabClickListener(view: View, position: Int) {

    }

    fun setTopicExpend(boolean: Boolean) {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        if (boolean) {
            iv_expand_arrow.setImageResource(R.mipmap.icon_topic_arrow_shrink)
            layoutParams.topMargin = DP2PX.dip2px(this, 10f)
            layoutParams.height = DP2PX.dip2px(this, 1100f)
        } else {
            iv_expand_arrow.setImageResource(R.mipmap.icon_topic_arrow_expend)
            layoutParams.topMargin = DP2PX.dip2px(this, 10f)
            layoutParams.height = DP2PX.dip2px(this, 500f)
        }
        rl_topic_content.layoutParams = layoutParams
    }

    /**
     * 小题得分数据展示初始化
     */
    fun initRecyclerViewScore() {
        iv_score_up.setOnClickListener {
            rv_list_score.scrollBy(0, -DP2PX.dip2px(this, 200f))
        }

        iv_score_down.setOnClickListener {
            rv_list_score.scrollBy(0, DP2PX.dip2px(this, 200f))
        }

        when (correctModule) {
            1, 2 -> {
                rv_list_score.layoutManager = GridLayoutManager(this, 3)
                mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_score, scoreMode, null).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        if (correctStatus == 1) {
                            setChangeItemScore(view, position)
                        }
                    }
                    rv_list_score.addItemDecoration(SpaceGridItemDeco(3, DP2PX.dip2px(this@BaseAppCompatActivity, 15f)))
                }
            }
            3, 4, 5 -> {
                rv_list_score.layoutManager = LinearLayoutManager(this)
                mTopicTwoScoreAdapter = TopicTwoScoreAdapter(if (correctModule == 5) R.layout.item_topic_multi_score else R.layout.item_topic_two_score, scoreMode, null).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        val item = currentScores[position]
                        //批改状态为已提交未批改 且 没有子题目才能执行
                        if (correctStatus == 1 && item.childScores.isNullOrEmpty()) {
                            setChangeItemScore(view, position)
                        }
                    }
                    setCustomItemChildClickListener { view, position,childAdapter, childPos ->
                        if (correctStatus == 1) {
                            val scoreItem = currentScores[position]
                            val childItem = scoreItem.childScores[childPos]
                            when (view.id) {
                                R.id.tv_score -> {
                                    if (scoreMode == 1) {
                                        NumberDialog(this@BaseAppCompatActivity, 2, "最大输入${childItem.label}", childItem.label).builder().setDialogClickListener {
                                            childItem.score = it
                                            childItem.result = ScoreItemUtils.getItemScoreResult(childItem)
                                            childAdapter.notifyItemChanged(childPos,"updateScore")

                                            scoreItem.score = ScoreItemUtils.getItemScoreTotal(scoreItem.childScores)
                                            scoreItem.result=ScoreItemUtils.getItemScoreResult(scoreItem)

                                            notifyItemChanged(position,"updateScore")
                                            setTotalScore()
                                        }
                                    }
                                }
                                R.id.iv_result -> {
                                    if (childItem.result == 0) {
                                        childItem.result = 1
                                    } else {
                                        childItem.result = 0
                                    }
                                    childItem.score = childItem.result * childItem.label
                                    childAdapter.notifyItemChanged(childPos,"updateScore")

                                    scoreItem.score = ScoreItemUtils.getItemScoreTotal(scoreItem.childScores)
                                    scoreItem.result=ScoreItemUtils.getItemScoreResult(scoreItem)

                                    notifyItemChanged(position,"updateScore")
                                    setTotalScore()
                                }
                            }
                        }
                    }
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@BaseAppCompatActivity, 15f)))
                }
            }
            6, 7 -> {
                rv_list_score.setHasFixedSize(true)
                val sharedPool = RecyclerView.RecycledViewPool()
                rv_list_score.setRecycledViewPool(sharedPool)
                rv_list_score.layoutManager = LinearLayoutManager(this)
                mTopicMultistageScoreAdapter = TopicMultistageScoreAdapter(R.layout.item_topic_two_score, scoreMode, null).apply {
                    rv_list_score.adapter = this
                    bindToRecyclerView(rv_list_score)
                    setOnItemChildClickListener { adapter, view, position ->
                        val item = currentScores[position]
                        //批改状态为已提交未批改 且 没有子题目才能执行
                        if (correctStatus == 1 && item.childScores.isNullOrEmpty()) {
                            setChangeItemScore(view, position)
                        }
                    }
                    setCustomItemChildClickListener(object : TopicMultistageScoreAdapter.OnItemChildClickListener {
                        override fun onParentClick(view: View, position: Int, twoAdapter: TopicTwoScoreAdapter, parentPosition: Int) {
                            val rootItem = currentScores[position]
                            val parentItem = rootItem.childScores[parentPosition]
                            if (correctStatus == 1 && parentItem.childScores.isNullOrEmpty()) {
                                when (view.id) {
                                    R.id.tv_score -> {
                                        if (scoreMode == 1) {
                                            NumberDialog(this@BaseAppCompatActivity, 2, "最大输入${parentItem.label}", parentItem.label).builder().setDialogClickListener {
                                                parentItem.score = it
                                                parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                                twoAdapter.notifyItemChanged(parentPosition,"updateScore")

                                                rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                                rootItem.result=ScoreItemUtils.getItemScoreResult(rootItem)

                                                notifyItemChanged(position,"updateScore")
                                                setTotalScore()
                                            }
                                        }
                                    }

                                    R.id.iv_result -> {
                                        if (parentItem.result == 0) {
                                            parentItem.result = 1
                                        } else {
                                            parentItem.result = 0
                                        }
                                        parentItem.score = parentItem.result * parentItem.label
                                        twoAdapter.notifyItemChanged(parentPosition,"updateScore")

                                        rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                        rootItem.result=ScoreItemUtils.getItemScoreResult(rootItem)

                                        notifyItemChanged(position,"updateScore")
                                        setTotalScore()
                                    }
                                }
                            }
                        }

                        override fun onChildClick(view: View, position: Int, twoAdapter: TopicTwoScoreAdapter, parentPosition: Int, childAdapter: ChildAdapter, childPos: Int) {
                            val rootItem = currentScores[position]
                            val parentItem = rootItem.childScores[parentPosition]
                            val childItem = parentItem.childScores[childPos]
                            if (correctStatus == 1) {
                                when (view.id) {
                                    R.id.tv_score -> {
                                        if (scoreMode == 1) {
                                            NumberDialog(this@BaseAppCompatActivity, 2, "最大输入${childItem.label}", childItem.label).builder().setDialogClickListener {
                                                childItem.score = it
                                                childItem.result = ScoreItemUtils.getItemScoreResult(childItem)
                                                childAdapter.notifyItemChanged(childPos,"updateScore")

                                                parentItem.score = ScoreItemUtils.getItemScoreTotal(parentItem.childScores)
                                                parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                                twoAdapter.notifyItemChanged(parentPosition,"updateScore")

                                                rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                                rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                                notifyItemChanged(position, "updateScore")
                                                setTotalScore()
                                            }
                                        }
                                    }

                                    R.id.iv_result -> {
                                        if (childItem.result == 0) {
                                            childItem.result = 1
                                        } else {
                                            childItem.result = 0
                                        }
                                        childItem.score = childItem.result * childItem.label
                                        childAdapter.notifyItemChanged(childPos,"updateScore")

                                        parentItem.score = ScoreItemUtils.getItemScoreTotal(parentItem.childScores)
                                        parentItem.result = ScoreItemUtils.getItemScoreResult(parentItem)
                                        twoAdapter.notifyItemChanged(parentPosition,"updateScore")

                                        rootItem.score = ScoreItemUtils.getItemScoreTotal(rootItem.childScores)
                                        rootItem.result = ScoreItemUtils.getItemScoreResult(rootItem)

                                        notifyItemChanged(position, "updateScore")
                                        setTotalScore()
                                    }
                                }
                            }
                        }
                    })
                    rv_list_score.addItemDecoration(SpaceItemDeco(DP2PX.dip2px(this@BaseAppCompatActivity, 15f)))
                }
            }
        }

    }

    /**
     * 设置adapter
     */
    fun setRecyclerViewScoreAdapter() {
        when (correctModule) {
            1, 2 -> {
                mTopicScoreAdapter?.setNewData(currentScores)
            }
            3, 4, 5 -> {
                mTopicTwoScoreAdapter?.setNewData(currentScores)
            }
            6, 7 -> {
                mTopicMultistageScoreAdapter?.setNewData(currentScores)
            }
        }
    }

    /**
     * 大题数据变化
     */
    private fun setChangeItemScore(view: View, position: Int) {
        val item = currentScores[position]
        when (view.id) {
            R.id.tv_score -> {
                if (scoreMode == 1) {
                    NumberDialog(this@BaseAppCompatActivity, 2, "最大输入${item.label}", item.label).builder().setDialogClickListener {
                        item.score = it
                        item.result = ScoreItemUtils.getItemScoreResult(item)
                        when (correctModule) {
                            1, 2 -> {
                                mTopicScoreAdapter?.notifyItemChanged(position)
                            }
                            3, 4, 5 -> {
                                mTopicTwoScoreAdapter?.notifyItemChanged(position,"updateScore")
                            }
                            6, 7 -> {
                                mTopicMultistageScoreAdapter?.notifyItemChanged(position,"updateScore")
                            }
                        }
                        setTotalScore()
                    }
                }
            }

            R.id.iv_result -> {
                if (item.result == 0) {
                    item.result = 1
                } else {
                    item.result = 0
                }
                item.score = item.result * item.label
                when (correctModule) {
                    1, 2 -> {
                        mTopicScoreAdapter?.notifyItemChanged(position)
                    }

                    3, 4, 5 -> {
                        mTopicTwoScoreAdapter?.notifyItemChanged(position)
                    }

                    6, 7 -> {
                        mTopicMultistageScoreAdapter?.notifyItemChanged(position)
                    }
                }
                setTotalScore()
            }
        }
    }

    /**
     * 总分变化
     */
    private fun setTotalScore() {
        if (tv_total_score != null) {
            var total = 0.0
            for (item in currentScores) {
                total += item.score
            }
            tv_total_score.text = total.toString()
        }
    }

    /**
     * 数据分析初始化
     */
    fun initRecyclerAnalyse() {
        iv_analyse_up.setOnClickListener {
            rv_list.scrollBy(0, -DP2PX.dip2px(this, 200f))
        }

        iv_analyse_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this, 200f))
        }

        if (correctModule < 3) {
            rv_list.layoutManager = GridLayoutManager(this, 2)//创建布局管理
            mAnalyseAdapter = ExamAnalyseAdapter(R.layout.item_exam_analyse_score, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setOnItemChildClickListener { adapter, view, position ->
                    val item = totalAnalyseItems[position]
                    if (view.id == R.id.tv_wrong_num) {
                        val students = item.wrongStudents
                        val titleStr = "第${item.sortStr}题 错误学生"
                        if (students.size > 0)
                            AnalyseUserDetailsDialog(this@BaseAppCompatActivity, titleStr, students).builder()
                    }
                }
            }
            rv_list.addItemDecoration(SpaceGridItemDeco(2, DP2PX.dip2px(this, 15f)))
        } else {
            rv_list.layoutManager = LinearLayoutManager(this)
            mAnalyseMultiAdapter = ExamAnalyseMultiAdapter(R.layout.item_exam_analyse_multi_score, null).apply {
                rv_list.adapter = this
                bindToRecyclerView(rv_list)
                setOnItemChildClickListener { adapter, view, position ->
                    val item = totalAnalyseItems[position]
                    if (item.childAnalyses.isNullOrEmpty()) {
                        if (view.id == R.id.tv_wrong_num) {
                            val students = item.wrongStudents
                            val titleStr = "第${item.sortStr}题 错误学生"
                            if (students.size > 0)
                                AnalyseUserDetailsDialog(this@BaseAppCompatActivity, titleStr, students).builder()
                        }
                    }
                }
                setCustomItemChildClickListener { position, view, childPosition ->
                    val item = totalAnalyseItems[position]
                    if (view.id == R.id.tv_wrong_num) {
                        val students = item.childAnalyses[childPosition].wrongStudents
                        val titleStr = "第${item.sortStr}大题 第${item.childAnalyses[childPosition].sortStr}小题 错误学生"
                        if (students.size > 0)
                            AnalyseUserDetailsDialog(this@BaseAppCompatActivity, titleStr, students).builder()
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
        barChart.description.isEnabled = false
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)
        barChart.setScaleEnabled(false)
        barChart.setFitBars(true)
        barChart.setTouchEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setMaxVisibleValueCount(30)

        //x轴设置
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.axisLineColor = Color.parseColor("#000000")
        xAxis.textColor = Color.parseColor("#000000")
        xAxis.textSize = 14f
        xAxis.setGranularity(1f)
        //设置x轴转行
        barChart.extraBottomOffset = 2 * 14f
        barChart.setXAxisRenderer(CustomXAxisRenderer(barChart.viewPortHandler, barChart.xAxis, barChart.getTransformer(YAxis.AxisDependency.LEFT)))

        // Y轴左边
        val leftAxis = barChart.axisLeft
        leftAxis.setAxisMinValue(0f) // 设置最小值
        leftAxis.setDrawGridLines(false)
        leftAxis.axisLineColor = Color.parseColor("#000000")
        leftAxis.textSize = 14f
        //右侧Y轴自定义值
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return ToolUtils.getFormatNum(value * 100, "#") + "%"
            }
        }

        //y轴右边设置
        val rightAxis = barChart.axisRight
        rightAxis.setAxisMinValue(0f) // 设置最小值
        rightAxis.setDrawGridLines(false) // 不绘制网格线
        rightAxis.isEnabled = false
    }


    class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) : XAxisRenderer(viewPortHandler, xAxis, trans) {
        override fun drawLabel(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
//        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees);//注释掉这个，否则坐标标签复写两次
            val lines = formattedLabel.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in lines.indices) {
                val vOffset = i * mAxisLabelPaint.textSize
                Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees)
            }
        }
    }

    /**
     * 设置柱状图
     */
    fun setChartView() {
        if (correctModule > 0) {
            val barEntries = mutableListOf<BarEntry>()
            val topicStrs = mutableListOf<String>()
            if (correctModule < 3) {
                for (item in totalAnalyseItems) {
                    topicStrs.add("${item.sortStr}")
                    barEntries.add(BarEntry(totalAnalyseItems.indexOf(item).toFloat(), item.scoreRate.toFloat()))
                }
            } else {
                var count = 0
                for (item in totalAnalyseItems) {
                    val childItems = item.childAnalyses
                    //当没有小题时候加入大题
                    if (!childItems.isNullOrEmpty()) {
                        for (i in 0 until childItems.size) {
                            topicStrs.add("${item.sortStr}\n${childItems[i].sortStr}")
                            barEntries.add(BarEntry((count + i).toFloat(), childItems[i].scoreRate.toFloat()))
                        }
                        count += childItems.size
                    } else {
                        topicStrs.add("${item.sortStr}")
                        barEntries.add(BarEntry(count.toFloat(), item.scoreRate.toFloat()))
                        count += 1
                    }
                }
            }

            if (barEntries.size < 30) {
                for (i in barEntries.size until 30) {
                    topicStrs.add("")
                    barEntries.add(BarEntry(i.toFloat(), 0f))
                }
            }

            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.barBorderWidth = 0.5f
            barDataSet.barBorderColor = Color.BLACK
            val barData = BarData(barDataSet)

            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(topicStrs)
            xAxis.labelCount = topicStrs.size

            barChart.data = barData
            barChart.setFitBars(false)
            barChart.invalidate()
        }

    }

    /**
     * 设置翻页
     */
    protected fun setPageNumber(total: Int) {
        if (ll_page_number != null) {
            pageCount = ceil(total.toDouble() / pageSize).toInt()
            if (total == 0) {
                ll_page_number.visibility = View.INVISIBLE
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
    fun getCurrentScreenPos(): Int {
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
    protected fun hideKeyboard() {
        KeyboardUtils.hideSoftKeyboard(this)
    }

    protected fun showToast(s: String) {
        SToast.showText(getCurrentScreenPos(), s)
    }

    protected fun showToast(resId: Int) {
        SToast.showText(getCurrentScreenPos(), resId)
    }

    fun showToast(screen: Int, s: String) {
        SToast.showText(screen, s)
    }

    fun showToast(screen: Int, sId: Int) {
        SToast.showText(screen, sId)
    }

    protected fun showLog(s: String) {
        Log.d("debug", s)
    }

    protected fun showLog(resId: Int) {
        Log.d("debug", getString(resId))
    }

    /**
     * 跳转活动 已经打开过则关闭
     */
    protected fun customStartActivity(intent: Intent) {
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
        SToast.showText(getCurrentScreenPos(), R.string.login_timeout)
        MethodManager.logout(this)
    }

    override fun hideLoading() {
        mDialog?.dismiss()
    }

    override fun showLoading() {
        mDialog!!.show()
    }

    override fun fail(screen: Int, msg: String) {
        showToast(screen, msg)
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

    open fun fetchData() {

    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(msgFlag: String) {
        when (msgFlag) {
            Constants.NETWORK_CONNECTION_COMPLETE_EVENT -> {
                onRefreshData()
            }

            else -> {
                onEventBusMessage(msgFlag)
            }
        }
    }

    /**
     * 刷新数据
     */
    open fun onRefreshData() {
    }

    /**
     * 收到eventbus事件处理
     */
    open fun onEventBusMessage(msgFlag: String) {
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!isClickExpand) {
            screenPos = getCurrentScreenPos()
        }
        initDialog()
        initChangeScreenData()
        isClickExpand = false
    }

    /**
     * 切屏后，重新初始化数据（用于数据请求弹框显示正确的位置）
     */
    open fun initChangeScreenData() {
    }

    open fun onCommonData() {

    }


}


