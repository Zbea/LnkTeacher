package com.bll.lnkteacher.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.fragment.testpaper.TestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.testpaper.TestPaperCorrectFragment
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade

class TestPaperManagerFragment : BaseMainFragment(){

    private var testPaperAssignFragment: TestPaperAssignFragment? = null
    private var testPaperCorrectFragment: TestPaperCorrectFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper_manager
    }

    override fun initView() {
        super.initView()
        setTitle(DataBeanManager.getIndexRightData()[2].name)
        showView(tv_grade)
        setImageManager(R.mipmap.icon_add)

        iv_manager?.setOnClickListener {
            if (grade==0)
            {
                showToast("请选择年级")
                return@setOnClickListener
            }
            showCreateTestPaperName()
        }

        testPaperAssignFragment = TestPaperAssignFragment()
        testPaperCorrectFragment = TestPaperCorrectFragment()
        switchFragment(lastFragment, testPaperAssignFragment)

        initTab()
    }

    override fun lazyLoad() {
        setGradeStr()
    }

    private fun initTab() {
        val strs=DataBeanManager.testPaperStrs
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
                setImageManager(R.mipmap.icon_add)
                switchFragment(lastFragment, testPaperAssignFragment)
            }

            1 -> {
                disMissView(iv_manager)
                switchFragment(lastFragment, testPaperCorrectFragment)
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
        InputContentDialog(requireContext(), 2,"输入测验卷名称").builder()
            .setOnDialogClickListener { str ->
                testPaperAssignFragment?.addType(str+"测验卷")
            }
    }

    override fun onGradeSelectorEvent() {
        testPaperAssignFragment?.changeGrade(grade)
        testPaperCorrectFragment?.changeGrade(grade)
    }

    override fun onRefreshData() {
        lazyLoad()
        testPaperAssignFragment?.onRefreshData()
        testPaperCorrectFragment?.onRefreshData()
    }

}