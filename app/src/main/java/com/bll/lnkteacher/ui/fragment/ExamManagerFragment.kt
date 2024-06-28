package com.bll.lnkteacher.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.fragment.exam.ExamCorrectFragment
import com.bll.lnkteacher.ui.fragment.exam.ExamListFragment
import kotlinx.android.synthetic.main.common_fragment_title.*

class ExamManagerFragment:BaseMainFragment() {

    private var examCorrectFragment: ExamCorrectFragment? = null
    private var examListFragment: ExamListFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_exam_manager
    }

    override fun initView() {
        super.initView()

        setTitle(R.string.main_exam_title)
        showView(tv_grade)

        examCorrectFragment = ExamCorrectFragment()
        examListFragment = ExamListFragment()

        switchFragment(lastFragment, examListFragment)

        initTab()
    }

    override fun lazyLoad() {
        setGradeStr()
    }

    private fun initTab() {
        val strs= arrayListOf("考试列表","考试批改")
        for (i in strs.indices){
            itemTabTypes.add(ItemTypeBean().apply {
                title=strs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when (position) {
            0 -> {
                showView(tv_grade)
                switchFragment(lastFragment, examListFragment)
            }
            1 -> {
                disMissView(tv_grade)
                switchFragment(lastFragment, examCorrectFragment)
            }
        }
        lastPosition = position
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

    override fun onGradeSelectorEvent() {
        examListFragment?.onChangeGrade(grade)
    }

}