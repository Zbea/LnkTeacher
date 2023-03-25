package com.bll.lnkteacher.ui.fragment

import PopupClick
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.Grade
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.presenter.MainPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkCorrectFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperCorrectFragment
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class TeachingFragment : BaseFragment(),IContractView.IMainView {

    private var mainPresenter= MainPresenter(this)
    private var homeworkAssignFragment: HomeworkAssignFragment? = null
    private var homeworkCorrectFragment: HomeworkCorrectFragment? = null
    private var testPaperAssignFragment: TestPaperAssignFragment? = null
    private var testPaperCorrectFragment: TestPaperCorrectFragment? = null
    private var gradePopup:PopupRadioList?=null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var homeworkPopBeans = mutableListOf<PopupBean>()
    private var grade=1

    override fun onClassList(groups: MutableList<ClassGroup>) {
        DataBeanManager.classGroups=groups
    }

    override fun onGroupList(groups: MutableList<Group>) {
        val schools= mutableListOf<Group>()
        val areas= mutableListOf<Group>()
        for (item in groups){
            if (item.type==2){
                schools.add(item)
            }
            else{
                areas.add(item)
            }
        }
        DataBeanManager.schoolGroups=schools
        DataBeanManager.areaGroups=areas
    }
    override fun onList(grades: MutableList<Grade>) {
        DataBeanManager.grades=grades
        grade=DataBeanManager.popupGrades[0].id
        tv_grade.text=DataBeanManager.popupGrades[0].name
    }



    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching
    }

    override fun initView() {
        setTitle(R.string.main_teaching_title)
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
        if (DataBeanManager.grades.size==0){
            mainPresenter.getGrades()
        }
        if (DataBeanManager.classGroups.size==0){
            mainPresenter.getClassGroups()
            mainPresenter.getGroups()
        }
    }

    //初始化数据
    private fun initData() {
        if (DataBeanManager.popupGrades.size>0){
            grade=DataBeanManager.popupGrades[0].id
            tv_grade.text=DataBeanManager.popupGrades[0].name
        }
        homeworkPopBeans.add(PopupBean(0, getString(R.string.teaching_pop_assign_details), true))
        homeworkPopBeans.add(PopupBean(1, getString(R.string.teaching_pop_create_book), false))
        homeworkPopBeans.add(PopupBean(2, getString(R.string.teaching_pop_create_reel), false))
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
                        homeworkAssignFragment?.showAssignDetails()
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
                    homeworkCorrectFragment?.changeGrade(grade)
                    testPaperCorrectFragment?.changeGrade(grade)
                }
        }
        else{
            gradePopup?.show()
        }

    }

    /**
     * 新增作业本
     */
    private fun showCreateHomeworkName(hint: String,typeId:Int) {
        InputContentDialog(requireContext(), hint).builder()
            .setOnDialogClickListener { str ->
                var subtype=2
                var typeName=getString(R.string.teaching_book)
                if (typeId==2){
                    subtype=1
                    typeName=getString(R.string.teaching_reel)
                }
                val homeworkType = TypeBean()
                homeworkType.name = str+ typeName
                homeworkType.subType = typeId
                homeworkAssignFragment?.addHomeworkType(homeworkType,subtype)
            }

    }


    /**
     * 新增作业本
     */
    private fun showCreateTestPaperName() {
        InputContentDialog(requireContext(), getString(R.string.teaching_pop_create_testpaper)).builder()
            .setOnDialogClickListener { str ->
                testPaperAssignFragment?.addType(str)
            }
    }


}