package com.bll.lnkteacher.ui.activity.exam

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.presenter.ExamCorrectPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamCorrectUserAdapter
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ScoreItemUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_homework_correct.tv_share
import kotlinx.android.synthetic.main.ac_testpaper_correct.ll_score
import kotlinx.android.synthetic.main.ac_testpaper_correct.ll_score_topic
import kotlinx.android.synthetic.main.ac_testpaper_correct.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_answer
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_save
import kotlinx.android.synthetic.main.ac_testpaper_correct.tv_total_score
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total

class ExamDetailsActivity:BaseDrawingActivity(),IContractView.IExamCorrectView{

    private var examBean: ExamList.ExamBean?=null
    private var examClassBean:ExamList.ExamClassBean?=null
    private val mPresenter= ExamCorrectPresenter(this,3)
    private var userItems= mutableListOf<ExamClassUserList.ClassUserBean>()

    private var mAdapter: ExamCorrectUserAdapter?=null
    private var posImage=0//当前图片下标
    private var posUser=0//当前学生下标
    private var currentImages= mutableListOf<String>()

    override fun onExamClassUser(classUserList: ExamClassUserList) {
        userItems.clear()
        for (item in classUserList.list){
            if (item.studentUrl.isNullOrEmpty()){
                item.status=3
            }
            else{
                item.status=1
            }
            if (item.teacherUrl.isNotEmpty()){
                item.status=2
            }
            userItems.add(item)
        }
        if (userItems.size>0){
            userItems[posUser].isCheck=true
            setContentView()
        }
        mAdapter?.setNewData(userItems)
    }

    override fun onShare() {
        showToast("分享成功")
    }
    
    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {
        screenPos=Constants.SCREEN_LEFT
        val classPos=intent.getIntExtra("classPos",-1)
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamList.ExamBean
        examClassBean= examBean?.classList?.get(classPos)
        correctModule=examBean?.questionType!!
        scoreMode=examBean?.questionMode!!
        if (!examBean?.answerUrl.isNullOrEmpty())
            answerImages= examBean?.answerUrl?.split(",") as MutableList<String>

        val map=HashMap<String,Any>()
        map["schoolExamJobId"]= examClassBean?.schoolExamJobId!!
        map["classId"]= examClassBean?.classId!!
        mPresenter.getExamClassUser(map)
    }

    override fun initView() {
        setPageTitle("考卷详情  ${examClassBean?.className}")
        setDisableTouchInput(true)
        disMissView(iv_tool,iv_catalog,iv_btn)

        if (answerImages.size>0){
            showView(tv_answer)
            tv_answer.setOnClickListener {
                ImageDialog(this,2,answerImages).builder()
            }
        }

        tv_share.setOnClickListener {
            CommonDialog(this).setContent("确定分享该学生考卷？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun ok() {
                    val userIds= mutableListOf<Int>()
                    for (item in userItems){
                        if (userItems.indexOf(item)!=posUser)
                            userIds.add(item.userId)
                    }
                    val map=HashMap<String,Any>()
                    map["type"]=3
                    map["id"]=userItems[posUser].id
                    map["userIds"]= userIds
                    mPresenter.share(map)
                }
            })
        }

        initRecyclerView()
        initRecyclerViewScore()

        onChangeExpandView()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,7)//创建布局管理
        mAdapter = ExamCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(7,  30))
            setOnItemClickListener { adapter, view, position ->
                if (position==posUser)
                    return@setOnItemClickListener
                userItems[posUser].isCheck=false
                posUser=position//设置当前学生下标
                val item=userItems[position]
                item.isCheck=true
                notifyDataSetChanged()
                posImage=0
                setContentView()
            }
        }
    }

    override fun onChangeExpandContent() {
        if (getImageSize()==1)
            return
        if (posImage>=getImageSize()-1&&getImageSize()>1)
            posImage=getImageSize()-2
        changeErasure()
        isExpand=!isExpand
        onChangeExpandView()
        onChangeContent()
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=if (isExpand)2 else 1
        }
        onChangeContent()
    }

    override fun onPageDown() {
        if (posImage<getImageSize()-1){
            posImage+=if (isExpand)2 else 1
        }
        onChangeContent()
    }

    /**
     * 设置切换内容展示
     */
    private fun setContentView(){
        val userItem=userItems[posUser]
        correctStatus=userItem.status

        when(correctStatus){
            1->{
                currentScores = if (correctModule>0&&!userItem.question.isNullOrEmpty()){
                    ScoreItemUtils.jsonListToModuleList(correctModule,ScoreItemUtils.questionToList(userItem.question))
                } else{
                    ScoreItemUtils.jsonListToModuleList(correctModule,ScoreItemUtils.questionToList(examBean?.question!!))
                }
                currentImages=ToolUtils.getImages(userItem.studentUrl)
                tv_total_score.text = ""
                disMissView(ll_score)
                onChangeContent()
            }
            2->{
                currentScores = if (correctModule>0&&!userItem.question.isNullOrEmpty()){
                    ScoreItemUtils.jsonListToModuleList(correctModule,ScoreItemUtils.questionToList(userItem.question))
                } else{
                    ScoreItemUtils.jsonListToModuleList(correctModule,ScoreItemUtils.questionToList(examBean?.question!!))
                }
                currentImages=ToolUtils.getImages(userItem.teacherUrl)
                tv_total_score.text = userItem.score.toString()
                showView(ll_score,tv_share)
                disMissView(tv_save)
                onChangeContent()
            }
            3->{
                currentImages= mutableListOf()
                disMissView(ll_score)
                tv_page.text=""
                tv_page_total.text=""
                v_content_a?.setImageResource(0)
                v_content_b?.setImageResource(0)
            }
        }

        if (correctModule>0&&correctStatus!=3){
            showView(ll_score_topic)
            setRecyclerViewScoreAdapter()
        }
        else{
            disMissView(ll_score_topic)
        }
    }

    /**
     * 设置学生提交图片展示
     */
    override fun onChangeContent(){
        if (currentImages.size==0){
            setContentImageClear()
            return
        }

        if (isExpand&&posImage>getImageSize()-2)
            posImage=getImageSize()-2
        if (isExpand&&posImage<0)
            posImage=0

        tv_page_total.text="${getImageSize()}"
        tv_page_total_a.text="${getImageSize()}"
        if (isExpand){
            GlideUtils.setImageUrl(this, currentImages[posImage],v_content_a)
            GlideUtils.setImageUrl(this, currentImages[posImage+1],v_content_b)
            tv_page.text="${posImage+1}"
            tv_page_a.text=if (posImage+1<getImageSize()) "${posImage+1+1}" else ""
        }
        else{
            GlideUtils.setImageUrl(this, currentImages[posImage],v_content_b)
            tv_page.text="${posImage+1}"
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize():Int{
        if (currentImages.isEmpty())
            return 0
        return currentImages.size
    }


}