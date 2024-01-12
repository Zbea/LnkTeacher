package com.bll.lnkteacher.ui.fragment

import PopupClick
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.HomeworkCorrectFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.TestPaperCorrectFragment
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class HomeworkManagerFragment : BaseFragment(){

    private var homeworkAssignFragment: HomeworkAssignFragment? = null
    private var homeworkCorrectFragment: HomeworkCorrectFragment? = null
    private var testPaperAssignFragment: TestPaperAssignFragment? = null
    private var testPaperCorrectFragment: TestPaperCorrectFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var homeworkPopBeans = mutableListOf<PopupBean>()
    private var popGrades= mutableListOf<PopupBean>()
    private var grade=0


    override fun getLayoutId(): Int {
        return R.layout.fragment_homework_manager
    }

    override fun initView() {
        setTitle(R.string.main_homework_title)
        showView(iv_manager,tv_grade)

        grade=SPUtil.getInt("grade")
        if (grade>0){
            tv_grade.text=DataBeanManager.getGradeStr(grade)
        }
        popGrades = if (grade==0) DataBeanManager.popupGrades else DataBeanManager.popupGrades(grade)

        homeworkPopBeans.add(PopupBean(0, getString(R.string.teaching_pop_assign_details), true))
        homeworkPopBeans.add(PopupBean(1, getString(R.string.teaching_pop_create_book), false))
        homeworkPopBeans.add(PopupBean(2, getString(R.string.teaching_pop_create_reel), false))

        homeworkAssignFragment = HomeworkAssignFragment()
        homeworkCorrectFragment = HomeworkCorrectFragment()
        testPaperAssignFragment = TestPaperAssignFragment()
        testPaperCorrectFragment = TestPaperCorrectFragment()

        switchFragment(lastFragment, homeworkAssignFragment)

        iv_manager?.setOnClickListener {
            if (lastPosition==2){
                if (grade==0)
                {
                    showToast("请选择年级")
                    return@setOnClickListener
                }
                showCreateTestPaperName()
            }
            else{
                showPopView()
            }
        }

        tv_grade.setOnClickListener {
            if (popGrades.size==0){
                popGrades =DataBeanManager.popupGrades
            }
            PopupRadioList(requireActivity(), popGrades, tv_grade,tv_grade.width,  5).builder().setOnSelectListener { item ->
                tv_grade?.text=item.name
                grade=item.id
                SPUtil.putInt("grade",grade)
                onGradeEvent()
            }
        }

        initTab()
        onGradeEvent()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity()))
            fetchCommonData()
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
                    showView(tv_grade,iv_manager)
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
                ft?.add(R.id.fl_content_homework, to)?.commit()
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
                if (grade==0)
                {
                    showToast("请选择年级")
                    return@setOnSelectListener
                }
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
     * 新增考试卷
     */
    private fun showCreateTestPaperName() {
        InputContentDialog(requireContext(), getString(R.string.teaching_pop_create_testpaper)).builder()
            .setOnDialogClickListener { str ->
                testPaperAssignFragment?.addType(str+"测验卷")
            }
    }

    private fun onGradeEvent() {
        homeworkAssignFragment?.changeGrade(grade)
        testPaperAssignFragment?.changeGrade(grade)
        homeworkCorrectFragment?.changeGrade(grade)
        testPaperCorrectFragment?.changeGrade(grade)
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}