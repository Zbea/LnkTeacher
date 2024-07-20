package com.bll.lnkteacher.ui.activity.teaching

import PopupClick
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ClassGroupChildCreateDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.*
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem.UserBean
import com.bll.lnkteacher.mvp.presenter.TestPaperAnalyseTeachingPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamAnalyseTeachingAdapter
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_classgroup_user.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse_teaching.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class AnalyseTeachingActivity:BaseDrawingActivity(),IContractView.IAnalyseTeachingView {

    private lateinit var mPresenter:TestPaperAnalyseTeachingPresenter
    private var classPos=0
    private var classId=0
    private var classGroupId=0
    private var classList= mutableListOf<TestPaperClassBean>()
    private var users= mutableListOf<UserBean>()
    private var scoreItems= mutableListOf<ScoreItem>()
    private var totalAnalyseItems= mutableListOf<AnalyseItem>() //题目集合
    private var mAdapter:ExamAnalyseTeachingAdapter?=null
    private var startScore=0
    private var endScore=100
    private var topicPops= mutableListOf<PopupBean>()//所有大题集合
    private var topicChildMap=HashMap<Int,List<PopupBean>>() //所有小题集合
    private var topicPosition=-1 //当前大题位置
    private var topicChildPosition=-1 //当前小题位置
    private var result=0//选中结果0错 1 正确
    private var resultChild=0

    override fun onClassPapers(bean: TestPaperClassUserList) {
        users.clear()
        scoreItems.clear()
        totalAnalyseItems.clear()

        for (userItem in bean.list) {
            if (userItem.status==2)
                users.add(UserBean(userItem.userId,userItem.name,userItem.score))
            correctModule = userItem.questionType
            if (!userItem.question.isNullOrEmpty() && userItem.status == 2 && correctModule > 0) {
                currentScores = jsonToList(userItem.question) as MutableList<ScoreItem>
                for (item in currentScores) {
                    val currentScore = MethodManager.getScore(item.score)
                    if (correctModule < 3) {
                        if (totalAnalyseItems.size < currentScores.size) {
                            val analyseItem = AnalyseItem()
                            setAnalyseData(userItem,item, analyseItem)
                            totalAnalyseItems.add(analyseItem)
                        } else {
                            val examAnalyseItem = totalAnalyseItems[item.sort - 1]
                            setAnalyseData(userItem,item, examAnalyseItem)
                        }
                    } else {
                        if (totalAnalyseItems.size<currentScores.size){
                            val analyseItem= AnalyseItem()
                            analyseItem.sort=item.sort
                            analyseItem.totalScore+=currentScore
                            analyseItem.num+=1
                            analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
                            if (currentScore<item.label){
                                analyseItem.wrongNum+=1
                                analyseItem.wrongStudents.add(UserBean(userItem.userId, userItem.name, userItem.score))
                            }
                            else{
                                analyseItem.rightNum+=1
                                analyseItem.rightStudents.add(UserBean(userItem.userId, userItem.name, userItem.score))
                            }

                            val childAnalyseItems= mutableListOf<AnalyseItem>()
                            for (childItem in item.childScores){
                                val childAnalyseItem= AnalyseItem()
                                setAnalyseData(userItem,childItem,childAnalyseItem)
                                childAnalyseItems.add(childAnalyseItem)
                            }
                            analyseItem.childAnalyses=childAnalyseItems
                            totalAnalyseItems.add(analyseItem)
                        }
                        else{
                            val analyseItem=totalAnalyseItems[item.sort-1]
                            analyseItem.totalScore+=currentScore
                            analyseItem.num+=1
                            analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
                            if (currentScore<item.label){
                                analyseItem.wrongNum+=1
                                analyseItem.wrongStudents.add(UserBean(userItem.userId, userItem.name, userItem.score))
                            }
                            else{
                                analyseItem.rightNum+=1
                                analyseItem.rightStudents.add(UserBean(userItem.userId, userItem.name, userItem.score))
                            }

                            for (childItem in item.childScores){
                                val index=item.childScores.indexOf(childItem)
                                val childExamAnalyseItem=analyseItem.childAnalyses[index]
                                setAnalyseData(userItem,childItem,childExamAnalyseItem)
                            }
                        }
                    }
                }
            }
        }

        if (correctModule>0){
            when(correctModule){
                1->{
                    disMissView(ll_topic_child)
                    for (i in 0 until totalAnalyseItems.size){
                            topicPops.add(PopupBean(i,"第 ${ToolUtils.getIntToChineseNumber(i+1)} 题"))
                    }
                }
                2->{
                    disMissView(ll_topic_child)
                    for (i in 0 until totalAnalyseItems.size){
                       topicPops.add(PopupBean(i,"第 ${i+1} 题"))
                    }
                }
                else->{
                    showView(ll_topic_child)
                    for (i in 0 until totalAnalyseItems.size){
                        topicPops.add(PopupBean(i,"第 ${ToolUtils.getIntToChineseNumber(i+1)} 题"))
                        //遍历子集，拿到对应的题目集合
                        val item=totalAnalyseItems[i]
                        val topicChildPops= mutableListOf<PopupBean>()
                        for (childPos in item.childAnalyses.indices){
                            val childItem=item.childAnalyses[childPos]
                            topicChildPops.add(PopupBean(childPos,"第 ${childItem.sort} 题 "))
                        }
                        topicChildMap[i]=topicChildPops
                    }
                }
            }
        }

        setChangeScoreUser()

    }

    override fun onCreateSuccess() {
        showToast("创建子群成功")
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }

    override fun onRefreshSuccess() {
        showToast("更新子群学生成功")
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse_teaching
    }

    override fun initData() {
        initChangeScreenData()
        classPos=intent.getIntExtra("classPos",-1)
        val correctList= intent.getBundleExtra("bundle")?.get("paperCorrect") as CorrectBean
        classList=correctList.examList!!
        correctModule=correctList.questionType
        for (item in classList){
            mExamClassGroups.add(ClassGroup().apply {
                classId=item.classId
                classGroupId=item.classGroupId
                name=item.name
            })
        }

        mExamClassGroups[classPos].isCheck=true
        classId=mExamClassGroups[classPos].classId
        classGroupId= mExamClassGroups[classPos].classGroupId
        mPresenter.getClassPapers(classList[classPos].examChangeId)
    }

    override fun initChangeScreenData() {
        mPresenter=TestPaperAnalyseTeachingPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("因材施教")
        setPageSetting("创建子群")
        setPageCustom("更新子群")

        if (correctModule<3){
            disMissView(ll_topic_child)
        }

        mClassAdapter?.setNewData(mExamClassGroups)

        tv_custom.setOnClickListener {
            val classGroups=getClassChild()
            if (classGroups.size==0)
            {
                showToast("当前班级不存在子群")
                return@setOnClickListener
            }
            val items= mutableListOf<ItemList>()
            for (item in classGroups){
                items.add(ItemList(item.classId,item.name))
            }

            val ids= mutableListOf<Int>()
            for (item in mAdapter?.data!!){
                ids.add(item.userId)
            }
            if (ids.size==0)
            {
                showToast("未选择学生")
                return@setOnClickListener
            }
            ItemSelectorDialog(this,"选择更新子群",items).builder().setOnDialogClickListener{
                val classGroup=classGroups[it]
                mPresenter.refreshGroupChild(classGroupId,classGroup.classId, ids)
            }

        }

        tv_setting.setOnClickListener {
            val ids= mutableListOf<Int>()
            for (item in mAdapter?.data!!){
                ids.add(item.userId)
            }
            if (ids.size==0)
            {
                showToast("未选择学生")
                return@setOnClickListener
            }
            val titleStr=tv_class.text.toString()+"(${mUser?.subjectName})"
            ClassGroupChildCreateDialog(this,titleStr).builder().setOnDialogClickListener { name ->
                mPresenter.createGroupChild (classGroupId, name,ids)
            }
        }

        tv_left_score.setOnClickListener {
            InputContentDialog(this,getCurrentScreenPos(),startScore.toString(),1).builder().setOnDialogClickListener{
                if (TextUtils.isDigitsOnly(it)) {
                    tv_left_score.text=it
                    startScore=MethodManager.getScore(it)
                    setChangeScoreUser()
                }
            }
        }

        tv_right_score.setOnClickListener {
            InputContentDialog(this,getCurrentScreenPos(),endScore.toString(),1).builder().setOnDialogClickListener{
                if (TextUtils.isDigitsOnly(it)) {
                    tv_right_score.text=it
                    endScore=MethodManager.getScore(it)
                    setChangeScoreUser()
                }
            }
        }

        tv_topic.setOnClickListener {
            PopupClick(this,topicPops,tv_topic,5).builder().setOnSelectListener{
                topicPosition=it.id
                tv_topic.text=it.name.replace("第","").replace("题","")
                //刷新子题目
                topicChildPosition=-1
                tv_topic_child.text=""
                setChangeTopicUser()
            }
        }

        tv_topic_child.setOnClickListener {
            if (topicPosition<0)
            {
                return@setOnClickListener
            }
            val childPopups=topicChildMap[topicPosition] as MutableList<PopupBean>
            PopupClick(this,childPopups ,tv_topic_child,5).builder().setOnSelectListener{
                topicChildPosition=it.id
                tv_topic_child.text=it.name.replace("第","").replace("题","")
                setChangeTopicUser()
            }
        }


        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            if (i==R.id.rb_score){
                setChangeScoreUser()
            }
            else{
                setChangeTopicUser()
            }
        }

        rg_topic.setOnCheckedChangeListener { radioGroup, i ->
            result=if (i==R.id.rb_wrong_topic){
                0
            }
            else{
                1
            }
            setChangeTopicUser()
        }

        rg_topic_child.setOnCheckedChangeListener { radioGroup, i ->
            resultChild=if (i==R.id.rb_wrong_topic){
                0
            }
            else{
                1
            }
            setChangeTopicChildUser()
        }

        initRecyclerView()
    }

    override fun onClassClickListener(view: View, position: Int) {
        if (classPos!=position){
            classId=mExamClassGroups[classPos].classId
            classGroupId= mExamClassGroups[classPos].classGroupId
            mPresenter.getClassPapers(classList[classPos].examChangeId)
        }
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,3)//创建布局管理
        mAdapter = ExamAnalyseTeachingAdapter(R.layout.item_exam_analyse_teaching_user, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(3,20))
    }

    /**
     * 获取当前班级子群
     */
    private fun getClassChild():MutableList<ClassGroup>{
        val classGroups= mutableListOf<ClassGroup>()
        for (item in DataBeanManager.classGroups){
            if (item.classGroupId==classGroupId&&item.state!=1){
                classGroups.add(item)
            }
        }
        return classGroups
    }

    /**
     * 筛选获取成绩变化学生变化
     */
    private fun setChangeScoreUser(){
        val currentUsers= mutableListOf<UserBean>()
        for (item in users){
            if (item.score in startScore..endScore){
                currentUsers.add(item)
            }
        }
        mAdapter?.setNewData(currentUsers)
    }

    /**
     * 筛选获取题目学生变化
     */
    private fun setChangeTopicUser(){
        var currentUsers= mutableListOf<UserBean>()
        if (correctModule>0&&result>=0){
            if (topicPosition>=0){
                currentUsers = if (result==1){
                    totalAnalyseItems[topicPosition].rightStudents
                } else{
                    totalAnalyseItems[topicPosition].wrongStudents
                }
            }
        }
        mAdapter?.setNewData(currentUsers)
    }

    /**
     * 筛选获取子题学生变化
     */
    private fun setChangeTopicChildUser(){
        var currentUsers= mutableListOf<UserBean>()
        if (correctModule>2){
            if (topicPosition>=0&&topicChildPosition>=0){
                currentUsers = if (result==1){
                    totalAnalyseItems[topicPosition].childAnalyses[topicChildPosition].rightStudents
                } else{
                    totalAnalyseItems[topicPosition].childAnalyses[topicChildPosition].wrongStudents
                }
            }
        }
        mAdapter?.setNewData(currentUsers)
    }

    /**
     * 数据分析赋值
     */
    private fun setAnalyseData(classUserBean: TestPaperClassUserList.ClassUserBean, scoreItem: ScoreItem, analyseItem: AnalyseItem){
        analyseItem.sort=scoreItem.sort
        analyseItem.totalScore+=MethodManager.getScore(scoreItem.score)
        analyseItem.num+=1
        if (scoreItem.result==0){
            analyseItem.wrongNum+=1
            analyseItem.wrongStudents.add(UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        else{
            analyseItem.rightNum+=1
            analyseItem.rightStudents.add(UserBean(classUserBean.userId, classUserBean.name, classUserBean.score))
        }
        analyseItem.averageScore=analyseItem.totalScore/analyseItem.num
    }
}