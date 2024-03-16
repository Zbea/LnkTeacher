package com.bll.lnkteacher.ui.fragment

import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.fragment.exam.ExamCorrectFragment
import com.bll.lnkteacher.ui.fragment.exam.ExamListFragment
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class ExamManagerFragment:BaseMainFragment() {

    private var examCorrectFragment: ExamCorrectFragment? = null
    private var examListFragment: ExamListFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null
    private var popGrades= mutableListOf<PopupBean>()
    private var grade=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_exam_manager
    }

    override fun initView() {
        setTitle(R.string.main_exam_title)
        showView(tv_grade)
        grade=SPUtil.getInt("grade")
        if (grade>0){
            tv_grade.text=DataBeanManager.getGradeStr(grade)
        }
        popGrades = if (grade==0) DataBeanManager.popupGrades else DataBeanManager.popupGrades(grade)

        examCorrectFragment = ExamCorrectFragment()
        examListFragment = ExamListFragment()

        switchFragment(lastFragment, examListFragment)

        tv_grade.setOnClickListener {
            PopupRadioList(requireActivity(), popGrades, tv_grade,tv_grade.width,  5).builder().setOnSelectListener { item ->
                tv_grade?.text=item.name
                SPUtil.putInt("grade",item.id)
            }
        }

        initTab()
    }

    override fun lazyLoad() {
        if (NetworkUtil.isNetworkAvailable(requireActivity()))
            fetchCommonData()
    }

    /**
     * 设置tab
     */
    private fun initTab() {
        val strs= arrayListOf("考试列表","考试批改")
        for (i in strs.indices){
            rg_group.addView(getRadioButton(i,strs[i],strs.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    showView(tv_grade)
                    switchFragment(lastFragment, examListFragment)
                }
                1 -> {
                    disMissView(tv_grade)
                    switchFragment(lastFragment, examCorrectFragment)
                }
            }
            lastPosition = i
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
                ft?.add(R.id.fl_content_exam, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}