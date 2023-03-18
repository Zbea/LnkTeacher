package com.bll.lnkteacher.ui.fragment

import PopupClick
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.HomeworkAssignDetailsDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.Grade
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass
import com.bll.lnkteacher.mvp.model.homework.HomeworkType
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperType
import com.bll.lnkteacher.mvp.presenter.TeachingPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkCorrectFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperCorrectFragment
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class TeachingFragment : BaseFragment(),IContractView.ITeachingView {

    private val mPresenter=TeachingPresenter(this)
    private var homeworkAssignFragment: HomeworkAssignFragment? = null
    private var homeworkCorrectFragment: HomeworkCorrectFragment? = null
    private var testPaperAssignFragment: TestPaperAssignFragment? = null
    private var testPaperCorrectFragment: TestPaperCorrectFragment? = null
    private var gradePopup:PopupRadioList?=null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var homeworkPopBeans = mutableListOf<PopupBean>()
    private var grade=1

    override fun onHomeworkType(types: MutableList<TestPaperType.TypeBean>?) {
        homeworkPopBeans.add(PopupBean(0, "布置详情", true))
        for (item in types!!){
            homeworkPopBeans.add(PopupBean(item.id, item.name, false))
        }
    }

    override fun onGrade(list: MutableList<Grade>) {
        DataBeanManager.grades=list
        grade=DataBeanManager.popupGrades[0].id
        tv_grade.text=DataBeanManager.popupGrades[0].name
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching
    }

    override fun initView() {
        setTitle("教学")
        showView(iv_manager,tv_grade)

        homeworkAssignFragment = HomeworkAssignFragment()
        homeworkCorrectFragment = HomeworkCorrectFragment()
        testPaperAssignFragment = TestPaperAssignFragment()
        testPaperCorrectFragment = TestPaperCorrectFragment()

        switchFragment(lastFragment, homeworkAssignFragment)

        iv_manager?.setOnClickListener {
            if (lastPosition==2){
                showCreateTestPaperName()
            }
            else{
                showPopView()
            }
        }
        tv_grade.setOnClickListener {
            showPopGradeView()
        }

        initData()
        initTab()
    }

    override fun lazyLoad() {
    }

    //初始化数据
    private fun initData() {
        if (DataBeanManager.popupGrades.size>0){
            grade=DataBeanManager.popupGrades[0].id
            tv_grade.text=DataBeanManager.popupGrades[0].name
        }
        else{
            mPresenter.getGrades()
        }
        homeworkPopBeans.add(PopupBean(0, "布置详情", true))
        homeworkPopBeans.add(PopupBean(1, "新增作业本", false))
        homeworkPopBeans.add(PopupBean(2, "新增作业卷", false))
    }

    /**
     * 设置tab
     */
    private fun initTab() {
        val strs=DataBeanManager.teachingStrs
        for (i in strs.indices){
            rg_group.addView(getRadioButton(i,strs[i],strs.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    showView(iv_manager,tv_grade)
                    iv_manager.setImageResource(R.mipmap.icon_manager)
                    switchFragment(lastFragment, homeworkAssignFragment)
                }

                1 -> {
                    disMissView(iv_manager,tv_grade)
                    switchFragment(lastFragment, homeworkCorrectFragment)
                }

                2 -> {
                    showView(iv_manager,tv_grade)
                    iv_manager.setImageResource(R.mipmap.icon_add)
                    switchFragment(lastFragment, testPaperAssignFragment)
                }

                3 -> {
                    disMissView(iv_manager,tv_grade)
                    switchFragment(lastFragment, testPaperCorrectFragment)
                }

            }
            this.lastPosition = i
        }

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
                ft?.add(R.id.fl_content, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    //顶部弹出pop选择框
    private fun showPopView() {
        PopupClick(requireActivity(), homeworkPopBeans, iv_manager,  10).builder()
            .setOnSelectListener { item ->
                when(item.id){
                    0->{
                        showHomeworkList()
                    }
                    1->{
                        showCreateHomeworkName(item.name, item.id)
                    }
                    else->{
                        showCreateHomeworkName(item.name, item.id)
                    }
                }
            }
    }

    //顶部弹出年级选择框
    private fun showPopGradeView() {
        if (gradePopup==null){
            gradePopup= PopupRadioList(requireActivity(), DataBeanManager.popupGrades, tv_grade,tv_grade.width,  10).builder()
            gradePopup?.setOnSelectListener { item ->
                    tv_grade.text=item.name
                    grade=item.id
                    homeworkAssignFragment?.changeGrade(grade)
                    testPaperAssignFragment?.changeGrade(grade)
                }
        }
        else{
            gradePopup?.show()
        }

    }


    /**
     * 作业布置详情
     */
    private fun showHomeworkList() {
        val items = mutableListOf<HomeworkAssignDetails>()
        val classs = mutableListOf<HomeworkClass>()

        val classGroupBean= HomeworkClass()
        classGroupBean.className="三年1班"
        classGroupBean.isCommit=true
        classGroupBean.date=System.currentTimeMillis()
        classs.add(classGroupBean)
        classs.add(classGroupBean)
        classs.add(classGroupBean)

        for (i in 0..3) {
            val item = HomeworkAssignDetails()
            item.typeName = "家庭作业本"
            item.date = System.currentTimeMillis()
            item.content = "语文作业1.3.5页第三题"
            item.list=classs
            items.add(item)
        }
        HomeworkAssignDetailsDialog(requireContext(), items).builder()
    }

    /**
     * 新增作业本
     */
    private fun showCreateHomeworkName(hint: String,typeId:Int) {
        InputContentDialog(requireContext(), hint).builder()
            .setOnDialogClickListener { str ->
                var subtype=2
                var typeName="作业本"
                if (typeId==2){
                    subtype=1
                    typeName="作业卷"
                }
                val homeworkType = HomeworkType.TypeBean()
                homeworkType.title = str+ typeName
                homeworkType.subType = typeId
                homeworkAssignFragment?.changeData(homeworkType,subtype)
            }

    }


    /**
     * 新增作业本
     */
    private fun showCreateTestPaperName() {
        InputContentDialog(requireContext(), "新增考试卷").builder()
            .setOnDialogClickListener { str ->
                testPaperAssignFragment?.addType(str)
            }
    }


}