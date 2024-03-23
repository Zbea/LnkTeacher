package com.bll.lnkteacher.ui.fragment

import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.ui.fragment.test.TestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.test.TestPaperCorrectFragment
import com.bll.lnkteacher.utils.NetworkUtil
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*

class TestpaperManagerFragment : BaseMainFragment(){

    private var testPaperAssignFragment: TestPaperAssignFragment? = null
    private var testPaperCorrectFragment: TestPaperCorrectFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper_manager
    }

    override fun initView() {
        super.initView()
        setTitle("测验卷")
        showView(tv_grade)
        setImageManager(R.mipmap.icon_add)

        testPaperAssignFragment = TestPaperAssignFragment()
        testPaperCorrectFragment = TestPaperCorrectFragment()

        switchFragment(lastFragment, testPaperAssignFragment)

        iv_manager?.setOnClickListener {
            if (grade==0)
            {
                showToast("请选择年级")
                return@setOnClickListener
            }
            showCreateTestPaperName()
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
        val strs=DataBeanManager.testPaperStrs
        for (i in strs.indices){
            rg_group.addView(getRadioButton(i,strs[i],strs.size-1))
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                0 -> {
                    showView(tv_grade)
                    setImageManager(R.mipmap.icon_add)
                    switchFragment(lastFragment, testPaperAssignFragment)
                }

                1 -> {
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
                ft?.add(R.id.fl_content_testpaper, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
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

    override fun onGradeEvent() {
        testPaperAssignFragment?.changeGrade(grade)
        testPaperCorrectFragment?.changeGrade(grade)
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}