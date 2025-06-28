package com.bll.lnkteacher.ui.activity.exam

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.ClassGroupChildCreateDialog
import com.bll.lnkteacher.dialog.ItemSelectorDialog
import com.bll.lnkteacher.dialog.NumberDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList.ExamBean
import com.bll.lnkteacher.mvp.model.exam.ExamList.ExamClassBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem.UserBean
import com.bll.lnkteacher.mvp.presenter.ExamAnalyseTeachingPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.ExamAnalyseTeachingAdapter
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.ac_classgroup_user.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_analyse_teaching.tv_left_score
import kotlinx.android.synthetic.main.ac_testpaper_analyse_teaching.tv_right_score
import kotlinx.android.synthetic.main.common_title.tv_custom
import kotlinx.android.synthetic.main.common_title.tv_custom_1
import org.greenrobot.eventbus.EventBus

class ExamAnalyseTeachingActivity:BaseDrawingActivity(),IContractView.IExamAnalyseTeachingView {

    private lateinit var mPresenter:ExamAnalyseTeachingPresenter
    private var examBean:ExamBean?=null
    private var classPos=0
    private var classId=0
    private var classGroupId=0
    private var classList= mutableListOf<ExamClassBean>()
    private var users= mutableListOf<UserBean>()
    private var mAdapter:ExamAnalyseTeachingAdapter?=null
    private var startScore=0.0
    private var endScore=100.0

    override fun onClassPapers(bean: ExamClassUserList) {
        users.clear()
        for (userItem in bean.list) {
            if (userItem.teacherUrl.isNotEmpty()) {
                users.add(UserBean(userItem.userId,userItem.studentName,userItem.score))
            }
        }
        setChangeScoreUser()
    }

    override fun onCreateSuccess() {
        showToast("创建层群成功")
        EventBus.getDefault().post(Constants.CLASSGROUP_INFO_EVENT)
    }

    override fun onRefreshSuccess() {
        showToast("更新层群学生成功")
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_analyse_teaching
    }

    override fun initData() {
        initChangeScreenData()
        classPos=intent.getIntExtra("classPos",-1)
        examBean= intent.getBundleExtra("bundle")?.get("examBean") as ExamBean
        classList=examBean!!.classList
        correctModule=examBean!!.questionType
        scoreMode=examBean!!.questionMode

        classId=classList[classPos].classId
        classGroupId= classList[classPos].classGroupId

        fetchClassUser()
    }

    override fun initChangeScreenData() {
        mPresenter=ExamAnalyseTeachingPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("因材施教  "+classList[classPos].className)
        showView(tv_custom,tv_custom_1)
        tv_custom.text="更新层群"
        tv_custom_1.text="创建层群"

        tv_custom.setOnClickListener {
            val classGroups=getClassChild()
            if (classGroups.size==0)
            {
                showToast("当前班级不存在层群")
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
            ItemSelectorDialog(this,"选择更新层群",items).builder().setOnDialogClickListener{
                val classGroup=classGroups[it]
                mPresenter.refreshGroupChild(classGroupId,classGroup.classId, ids)
            }

        }

        tv_custom_1.setOnClickListener {
            val ids= mutableListOf<Int>()
            for (item in mAdapter?.data!!){
                ids.add(item.userId)
            }
            if (ids.size==0)
            {
                showToast("未选择学生")
                return@setOnClickListener
            }
            val titleStr=classList[classPos].className+"(${mUser?.subjectName})"
            ClassGroupChildCreateDialog(this,titleStr).builder().setOnDialogClickListener { name ->
                mPresenter.createGroupChild (classGroupId, name,ids)
            }
        }

        tv_left_score.setOnClickListener {
            NumberDialog(this,getCurrentScreenPos(),startScore.toString()).builder().setDialogClickListener{
                tv_left_score.text=it.toString()
                startScore=it
                setChangeScoreUser()
            }
        }

        tv_right_score.setOnClickListener {
            NumberDialog(this,getCurrentScreenPos(),endScore.toString()).builder().setDialogClickListener{
                tv_right_score.text=it.toString()
                endScore=it
                setChangeScoreUser()
            }
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = GridLayoutManager(this,3)//创建布局管理
        mAdapter = ExamAnalyseTeachingAdapter(R.layout.item_exam_analyse_teaching_user, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(3,10))
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
     * 获取班级学生列表
     */
    private fun fetchClassUser(){
        mPresenter.getExamPaperClassPapers(examBean?.id!!, classList[classPos].classId)
    }

    override fun onRefreshData() {
        fetchClassUser()
    }
}