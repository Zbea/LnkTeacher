package com.bll.lnkteacher.base

import PopupClick
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.dialog.*
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.ExamScoreItem
import com.bll.lnkteacher.ui.adapter.NumberListAdapter
import com.bll.lnkteacher.ui.adapter.TopicMultiScoreAdapter
import com.bll.lnkteacher.ui.adapter.TopicScoreAdapter
import com.bll.lnkteacher.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.common_drawing_geometry.*
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.util.regex.Pattern


abstract class BaseDrawingActivity : BaseActivity(){

    var isExpand=false
    var elik_a: EinkPWInterface? = null
    var elik_b: EinkPWInterface? = null
    var isErasure=false
    var mTopicScoreAdapter: TopicScoreAdapter?=null
    var mTopicMultiAdapter: TopicMultiScoreAdapter?=null
    var currentScores=mutableListOf<ExamScoreItem>()//初始题目分数
    var positionScore=0//分数指示
    var positionScoreChild=0//小题分数指示
    var correctModule=-1//批改摸排
    private var circlePos=0
    private var axisPos=0
    private var isGeometry=false//是否处于几何绘图
    private var isParallel=false//是否选中平行
    private var isCurrent=false//当前支持的几何绘图笔形
    private var isScale=false//是否选中刻度
    private var revocationList= mutableListOf<Int>()
    private var currentGeometry=0
    private var currentDrawObj=PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN//当前笔形

    override fun initCreate() {
        onInStanceElik()

        if (iv_top!=null){
            elik_a?.addOnTopView(iv_top)
            elik_b?.addOnTopView(iv_top)
        }
        initClick()
        initGeometryView()
        initScoreView()
    }

    open fun onInStanceElik(){
        if (v_content_a!=null && v_content_b!=null){
            elik_a = v_content_a?.pwInterFace
        }
        if (v_content_b!=null){
            elik_b = v_content_b?.pwInterFace
        }
    }

    private fun initClick(){
        iv_tool?.setOnClickListener {
            showDialogAppTool()
        }

        iv_erasure?.setOnClickListener {
            isErasure=!isErasure
            if (isErasure){
                iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure_big)
                onErasure()
            }
            else{
                stopErasure()
            }
        }

        iv_page_up?.setOnClickListener {
            onPageUp()
        }

        iv_page_down?.setOnClickListener {
            onPageDown()
        }

        iv_catalog?.setOnClickListener {
            onCatalog()
        }

        iv_expand?.setOnClickListener {
            isClickExpand=true
            onChangeExpandContent()
        }
    }

    private fun initScoreView(){

        if (rv_list_number!=null){
            val numbers= mutableListOf<ItemList>()
            for (i in 0..50){
                numbers.add(ItemList(i,i.toString()))
            }
            rv_list_number.layoutManager = GridLayoutManager(this,17)//创建布局管理
            NumberListAdapter(R.layout.item_number, numbers).apply {
                rv_list_number.adapter = this
                bindToRecyclerView(rv_list_number)
                setOnItemClickListener { adapter, view, position ->
                    val item= data[position]
                    if (correctModule<3){
                        currentScores[positionScore].score=item.id.toString()
                        mTopicScoreAdapter?.notifyItemChanged(positionScore)
                    }
                    else{
                        currentScores[positionScore].childScores[positionScoreChild].score=item.id.toString()
                        var num=0
                        for (ite in currentScores[positionScore].childScores){
                            if (!ite.score.isNullOrEmpty())
                                num += ite.score.toInt()
                        }
                        currentScores[positionScore].score=num.toString()
                        mTopicMultiAdapter?.notifyItemChanged(positionScore)
                    }
                    var num=0
                    for (ite in currentScores){
                        if (!ite.score.isNullOrEmpty())
                            num += ite.score.toInt()
                    }
                    tv_score_num?.text=num.toString()
                }
            }
        }

        if (rv_list_score!=null&&correctModule>0){
            initRecyclerViewScore()
        }
    }

    fun initRecyclerViewScore(){
        if (correctModule<3){
            rv_list_score.layoutManager = GridLayoutManager(this,5)
            mTopicScoreAdapter = TopicScoreAdapter(R.layout.item_topic_score,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setOnItemChildClickListener { adapter, view, position ->
                    positionScore=position
                    for (item in currentScores){
                        item.isCheck=false
                    }
                    currentScores[positionScore].isCheck=true
                    mTopicScoreAdapter?.notifyDataSetChanged()
                }
            }
        }
        else{
            rv_list_score.layoutManager = LinearLayoutManager(this)
            mTopicMultiAdapter = TopicMultiScoreAdapter(R.layout.item_topic_multi_score,null).apply {
                rv_list_score.adapter = this
                bindToRecyclerView(rv_list_score)
                setCustomItemChildClickListener{ position, view, childPos ->
                    positionScore=position
                    positionScoreChild=childPos
                    for (item in currentScores){
                        for (ite in item.childScores){
                            ite.isCheck=false
                        }
                    }
                    currentScores[position].childScores[childPos].isCheck=true
                    mTopicMultiAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 格式序列化 转行成一、二维数组
     */
   fun toJson(list:List<ExamScoreItem>):String{
        return if (correctModule<3){
            for (item in list){
                item.isCheck=false
            }
            Gson().toJson(list)
        } else{
            val items= arrayListOf <List<ExamScoreItem>>()
            for (item in list){
                for (ite in item.childScores){
                    ite.isCheck=false
                }
                items.add(item.childScores)
            }
            Gson().toJson(items)
        }
    }

    fun jsonToList(json:String):List<ExamScoreItem>{
        var items= mutableListOf<ExamScoreItem>()
        if (correctModule<3){
            items=Gson().fromJson(json, object : TypeToken<List<ExamScoreItem>>() {}.type) as MutableList<ExamScoreItem>
        }
        else{
            val scores=Gson().fromJson(json, object : TypeToken<List<List<ExamScoreItem>>>() {}.type) as MutableList<List<ExamScoreItem>>
            for (i in scores.indices){
                items.add(ExamScoreItem().apply {
                    sort=i+1
                    var totalItem=0
                    for (item in scores[i]){
                        if (!item.score.isNullOrEmpty()){
                            totalItem+=item.score.toInt()
                        }
                    }
                    score=totalItem.toString()
                    childScores=scores[i]
                })
            }
        }
        return items;
    }

    /**
     * 几何绘图
     */
    private fun initGeometryView(){

        val popsCircle= mutableListOf<PopupBean>()
        popsCircle.add(PopupBean(0,getString(R.string.circle_1),R.mipmap.icon_geometry_circle_1))
        popsCircle.add(PopupBean(1,getString(R.string.circle_2),R.mipmap.icon_geometry_circle_2))
        popsCircle.add(PopupBean(2,getString(R.string.circle_3),R.mipmap.icon_geometry_circle_3))

        val popsAxis= mutableListOf<PopupBean>()
        popsAxis.add(PopupBean(0,getString(R.string.axis_one),R.mipmap.icon_geometry_axis_1))
        popsAxis.add(PopupBean(1,getString(R.string.axis_two),R.mipmap.icon_geometry_axis_2))
        popsAxis.add(PopupBean(2,getString(R.string.axis_three),R.mipmap.icon_geometry_axis_3))

        val pops= mutableListOf<PopupBean>()
        pops.add(PopupBean(0,getString(R.string.line_black),false))
        pops.add(PopupBean(1,getString(R.string.line_gray),false))
        pops.add(PopupBean(2,getString(R.string.line_dotted),false))

        iv_geometry?.setOnClickListener {
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            disMissView(iv_geometry)
        }

        iv_line?.setOnClickListener {
            setCheckView(ll_line)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_LINE)
            currentGeometry=1
        }

        iv_rectangle?.setOnClickListener {
            setCheckView(ll_rectangle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RECTANGLE)
            currentGeometry=2
        }

        tv_circle?.setOnClickListener {
            PopupClick(this,popsCircle,tv_circle,5).builder().setOnSelectListener{
                iv_circle.setImageResource(it.resId)
                circlePos=it.id
                setEilkCircle()
            }
        }

        iv_circle?.setOnClickListener {
            setEilkCircle()
        }

        iv_arc?.setOnClickListener {
            setCheckView(ll_arc)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ARC)
            currentGeometry=4
        }

        iv_oval?.setOnClickListener {
            setCheckView(ll_oval)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_OVAL)
            currentGeometry=5
        }

        iv_vertical?.setOnClickListener {
            setCheckView(ll_vertical)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_VERTICALLINE)
            currentGeometry=6
        }

        iv_parabola?.setOnClickListener {
            setCheckView(ll_parabola)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_PARABOLA)
            currentGeometry=7
        }

        iv_angle?.setOnClickListener {
            setCheckView(ll_angle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ANGLE)
            currentGeometry=8
        }

        tv_axis?.setOnClickListener {
            PopupClick(this,popsAxis,tv_axis,5).builder().setOnSelectListener{
                iv_axis?.setImageResource(it.resId)
                axisPos=it.id
                setEilkAxis()
            }
        }

        iv_axis?.setOnClickListener {
            setEilkAxis()
        }

        iv_pen?.setOnClickListener {
            setDrawing()
        }

        tv_revocation?.setOnClickListener {
            if (revocationList.size>0){
                val type=revocationList.last()
                if (type==1)
                {
                    elik_a?.unDo()
                }
                else{
                    elik_b?.unDo()
                }
                revocationList.removeLast()
            }
        }

        tv_gray_line?.setOnClickListener {
            if (!isGeometry){
                return@setOnClickListener
            }
            PopupClick(this,pops,tv_gray_line,5).builder().setOnSelectListener{
                tv_gray_line?.text=it.name
                setLine(it.id)
            }
        }

        tv_scale?.setOnClickListener {
            if (isErasure){
                stopErasure()
            }
            tv_scale.isSelected=!tv_scale.isSelected
            isScale=tv_scale.isSelected
            tv_scale.setTextColor(getColor(if (isScale) R.color.white else R.color.black))
            if (currentGeometry==9){
                setEilkAxis()
            }
        }

        tv_parallel?.setOnClickListener {
            if (isErasure){
                stopErasure()
            }
            tv_parallel.isSelected=!isParallel
            isParallel=tv_parallel.isSelected
            tv_parallel.setTextColor(getColor(if (isParallel) R.color.white else R.color.black))
        }

        tv_reduce?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry)
            showView(iv_geometry)
            setViewElikUnable(iv_geometry)
            if (isParallel){
                tv_parallel?.callOnClick()
            }
            if (isScale){
                tv_scale?.callOnClick()
            }
        }

        tv_out?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry,iv_geometry)
            if (isParallel){
                tv_parallel?.callOnClick()
            }
            if (isScale){
                tv_scale?.callOnClick()
            }
        }

        elik_a?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_a?.setShifted(isCurrent&&isParallel)
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(1)
                if (revocationList.size>2)
                    revocationList.remove(0)
                if (elik_a?.curDrawObjStatus == true){
                    reDrawGeometry(elik_a!!,1)
                }
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik_a?.saveBitmap(true) {}
                onElikSava_a()
            }
        })

        elik_b?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_b?.setShifted(isCurrent&&isParallel)
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(2)
                if (revocationList.size>2)
                    revocationList.remove(0)
                if (elik_b?.curDrawObjStatus == true){
                    reDrawGeometry(elik_b!!,2)
                }
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik_b?.saveBitmap(true) {}
                onElikSava_b()
            }
        })

        this.setTouchAsFocus(true)
    }

    /**
     * 设置刻度重绘
     */
    private fun reDrawGeometry(elik:EinkPWInterface,location: Int){
        if (isErasure)
            return
        if (isScale){
            if (currentGeometry==1||currentGeometry==2||currentGeometry==3||currentGeometry==5||currentGeometry==7||currentGeometry==8||currentGeometry==9){
                Handler().postDelayed({
                    if (location==1){
                        v_content_a.invalidate()
                    }
                    else{
                        v_content_b.invalidate()
                    }
                    GeometryScaleDialog(this,currentGeometry,circlePos,location).builder()
                        ?.setOnDialogClickListener{
                                width, height ->
                            when (currentGeometry) {
                                2, 5 -> {
                                    elik.reDrawShape(width,height)
                                }
                                7->{
                                    val info=elik.curHandlerInfo
                                    elik.reDrawShape(if (setA(info)>0) width else -width ,info.split("&")[1].toFloat())
                                }
                                9 -> {
                                    elik.reDrawShape(width,5f)
                                }
                                else -> {
                                    elik.reDrawShape(width,-1f)
                                }
                            }
                        }
                },300)
            }
        }
    }

    /**
     * 获取a值
     */
    private fun setA(info:String):Float{
        val list= mutableListOf<String>()
        val pattern= Pattern.compile("-?\\d+(\\.\\d+)") // 编译正则表达式，匹配连续的数字
        val matcher= pattern.matcher(info) // 创建匹配器对象
        while (matcher.find()){
            list.add(matcher.group())
        }
        return list[0].toFloat()
    }

    /**
     * 画圆
     */
    private fun setEilkCircle(){
        setCheckView(ll_circle)
        when(circlePos){
            0->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE)
            1->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE2)
            else->setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE3)
        }
        currentGeometry=3
    }

    /**
     * 画坐标
     */
    private fun setEilkAxis(){
        setCheckView(ll_axis)
        when(axisPos){
            0->{
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS)
                elik_a?.setDrawAxisProperty(1, 10, 5,isScale)
                elik_b?.setDrawAxisProperty(1, 10, 5,isScale)
            }
            1->{
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS2)
                elik_a?.setDrawAxisProperty(2, 10, 5,isScale)
                elik_b?.setDrawAxisProperty(2, 10, 5,isScale)
            }
            2->{
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS3)
                elik_a?.setDrawAxisProperty(3, 10, 5,isScale)
                elik_b?.setDrawAxisProperty(3, 10, 5,isScale)
            }
        }
        currentGeometry=9
    }

    /**
     * 设置线
     */
    private fun setLine(type: Int){
        when(type){
            0->{
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(0)
            }
            1->{
                elik_a?.penColor = Color.parseColor("#999999")
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.parseColor("#999999")
                elik_b?.setPaintEffect(0)
            }
            else->{
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(1)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(1)
            }
        }
    }

    /**
     * 设置选中笔形
     */
    private fun setCheckView(view:View){
        if (isErasure){
            stopErasure()
        }
        if (view!=ll_pen){
            isGeometry=true
        }
        //当前支持平行的view
        isCurrent = view==ll_line||view==ll_angle||view==ll_axis
        ll_line?.setBackgroundResource(R.color.color_transparent)
        ll_rectangle?.setBackgroundResource(R.color.color_transparent)
        ll_circle?.setBackgroundResource(R.color.color_transparent)
        ll_arc?.setBackgroundResource(R.color.color_transparent)
        ll_oval?.setBackgroundResource(R.color.color_transparent)
        ll_vertical?.setBackgroundResource(R.color.color_transparent)
        ll_parabola?.setBackgroundResource(R.color.color_transparent)
        ll_angle?.setBackgroundResource(R.color.color_transparent)
        ll_axis?.setBackgroundResource(R.color.color_transparent)
        ll_pen?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_geometry_select)
    }

    /**
     * 设置笔类型
     */
    private fun setDrawOjectType(type:Int){
        elik_a?.drawObjectType = type
        elik_b?.drawObjectType = type
        if (type!=PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
            currentDrawObj=type
    }

    /**
     * 工具栏弹窗
     */
    private fun showDialogAppTool(){
        AppToolDialog(this,getCurrentScreenPos()).builder().setDialogClickListener{
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            if (isErasure)
                stopErasure()
        }
    }

    /**
     * 设置不能手写
     */
    fun setViewElikUnable(view:View){
        elik_a?.addOnTopView(view)
        elik_b?.addOnTopView(view)
    }

    /**
     * 下一页
     */
    open fun onPageDown(){
    }
    /**
     * 上一页
     */
    open fun onPageUp(){
    }

    /**
     * 打开目录
     */
    open fun onCatalog(){
    }

    /**
     * 左屏抬笔
     */
    open fun onElikSava_a(){
    }

    /**
     * 右屏抬笔
     */
    open fun onElikSava_b(){
    }


    /**
     * 设置擦除
     */
    private fun onErasure(){
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
    }

    /**
     * 结束擦除
     * （在展平、收屏时候都结束擦除）
     */
    private fun stopErasure(){
        isErasure=false
        //关闭橡皮擦
        iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure)
        setDrawOjectType(currentDrawObj)
    }

    /**
     * 恢复手写
     */
    private fun setDrawing(){
        setCheckView(ll_pen)
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN)
        currentGeometry=0
        //设置黑线
        setLine(0)
        tv_gray_line?.text=getString(R.string.line_black)

        isGeometry=false
    }

    /**
     * 自动收屏后自动取消橡皮擦
     */
    fun changeErasure(){
        if (isErasure){
            isErasure=false
            stopErasure()
        }
    }

    /**
     * 单双屏切换以及创建新数据
     */
    fun onChangeExpandView() {
        ll_content_a.visibility = if (isExpand) View.VISIBLE else View.GONE
    }

    /**
     * 单双屏切换
     */
    open fun onChangeExpandContent(){
    }

    /**
     * 内容变化
     */
    open fun onChangeContent(){
    }

    /**
     * 单双屏展开
     */
    fun moveToScreen(isExpand:Boolean){
        moveToScreen(if (isExpand) 3 else screenPos )
    }

    /**
     * 换屏 0默认左屏幕 1左屏幕 2右屏幕 3全屏
     */
    fun moveToScreen(scree: Int){
        moveToScreenPanel(scree)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        //屏蔽长按切焦点造成原手写翻页
        if (getKeyEventStatus()==17||getKeyEventStatus()==34){
            return super.onKeyUp(keyCode, event)
        }
        when(keyCode){
            KeyEvent.KEYCODE_PAGE_DOWN->{
                onPageDown()
            }
            KeyEvent.KEYCODE_PAGE_UP->{
                onPageUp()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

}


