package com.bll.lnkteacher.ui.fragment

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.HomeworkAssignDetailsDialog
import com.bll.lnkteacher.dialog.HomeworkCreateTypeDialog
import com.bll.lnkteacher.dialog.PopWindowRadioList
import com.bll.lnkteacher.mvp.model.HomeworkAssign
import com.bll.lnkteacher.mvp.model.HomeworkType
import com.bll.lnkteacher.mvp.model.PopWindowBean
import com.bll.lnkteacher.mvp.model.TestPaperType
import com.bll.lnkteacher.ui.fragment.teaching.TeachingHomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.TeachingHomeworkWorkFragment
import com.bll.lnkteacher.ui.fragment.teaching.TeachingTestPaperAssignFragment
import com.bll.lnkteacher.ui.fragment.teaching.TeachingTestPaperWorkFragment
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_teaching.*
import java.util.*

class TeachingFragment : BaseFragment() {

    private var homeworkAssignFragment: TeachingHomeworkAssignFragment? = null
    private var homeworkWorkFragment: TeachingHomeworkWorkFragment? = null
    private var testPaperAssignFragment: TeachingTestPaperAssignFragment? = null
    private var testPaperWorkFragment: TeachingTestPaperWorkFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var homeworkPopBeans = mutableListOf<PopWindowBean>()
    private var testPaperPopBeans = mutableListOf<PopWindowBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching
    }

    override fun initView() {
        setTitle("教学")
        showSearch(false)

        homeworkAssignFragment = TeachingHomeworkAssignFragment()
        homeworkWorkFragment = TeachingHomeworkWorkFragment()
        testPaperAssignFragment = TeachingTestPaperAssignFragment()
        testPaperWorkFragment = TeachingTestPaperWorkFragment()

        switchFragment(lastFragment, homeworkAssignFragment)

        initDate()
        initTab()
        onBindClick()
    }

    override fun lazyLoad() {
    }

    private fun initDate() {

        homeworkPopBeans.add(PopWindowBean(0, "布置详情", true))
        homeworkPopBeans.add(PopWindowBean(1, "新增作业本", false))
        homeworkPopBeans.add(PopWindowBean(2, "新增作业卷", false))

        testPaperPopBeans.add(PopWindowBean(0, "布置详情", true))
        testPaperPopBeans.add(PopWindowBean(1, "新增考试卷", false))

    }

    private fun onBindClick() {
        ivManagers?.setOnClickListener {
            showPopView()
        }
    }

    /**
     * 设置tab
     */
    private fun initTab() {
        val strs=DataBeanManager.getIncetance().teachingStrs
        for (i in strs.indices){
            var radioButton = layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
            radioButton.id = i
            radioButton.text = strs[i]
            radioButton.isChecked = i == 0
            var layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                DP2PX.dip2px(activity, 45f))
            layoutParams.marginEnd = if (i == strs.size-1) 0 else DP2PX.dip2px(activity, 44f)
            radioButton.layoutParams = layoutParams
            rg_group.addView(radioButton)
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            val position = i
            when (position) {
                0 -> {
                    showView(iv_manager)
                    switchFragment(lastFragment, homeworkAssignFragment)
                }

                1 -> {
                    disMissView(iv_manager)
                    switchFragment(lastFragment, homeworkWorkFragment)
                }

                2 -> {
                    showView(iv_manager)
                    switchFragment(lastFragment, testPaperAssignFragment)
                }

                3 -> {
                    disMissView(iv_manager)
                    switchFragment(lastFragment, testPaperWorkFragment)
                }

            }
            lastPosition = position!!
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

        val popBeans = when (lastPosition) {
            0, 1 -> {
                homeworkPopBeans
            }
            else -> {
                testPaperPopBeans
            }
        }
        PopWindowRadioList(requireActivity(), popBeans, ivManagers!!,  10).builder()
            ?.setOnSelectListener { item ->
                when (lastPosition) {
                    0, 1 -> {
                        when (item.id) {
                            0 -> showHomeworkList()
                            1 -> showCreateHomeworkName("作业本名称", 0)
                            2 -> showCreateHomeworkName("作业卷集名称", 1)
                        }
                    }
                    2, 3 -> {
                        when (item.id) {
                            0 -> showHomeworkList()
                            1 -> showCreateTestPaperName()
                        }
                    }
                }
            }
    }



    /**
     * 作业布置详情
     */
    private fun showHomeworkList() {
        val items = mutableListOf<HomeworkAssign>()
        val classs = mutableListOf<HomeworkAssign.ClassGroupBean>()

        val classGroupBean=HomeworkAssign().ClassGroupBean()
        classGroupBean.className="三年1班"
        classGroupBean.isCommit=true
        classGroupBean.date=System.currentTimeMillis()
        classs.add(classGroupBean)
        classs.add(classGroupBean)
        classs.add(classGroupBean)

        for (i in 0..3) {
            var item = HomeworkAssign()
            item.homeworkType = "家庭作业本"
            item.date = System.currentTimeMillis()
            item.content = "语文作业1.3.5页第三题"
            item.lists=classs
            items.add(item)
        }
        HomeworkAssignDetailsDialog(requireContext(), items).builder()
    }

    /**
     * 新增作业本
     */
    private fun showCreateHomeworkName(hint: String,type:Int) {
        HomeworkCreateTypeDialog(requireContext(), hint).builder()
            .setOnDialogClickListener { str ->
                val covers = DataBeanManager.getIncetance().homeworkCover
                val index = Random().nextInt(covers.size)
                var homeworkType = HomeworkType()
                homeworkType.name = str
                homeworkType.resId = covers[index].resId
                homeworkType.state = type
                homeworkAssignFragment?.refreshData(homeworkType)
            }

    }


    /**
     * 新增作业本
     */
    private fun showCreateTestPaperName() {
        HomeworkCreateTypeDialog(requireContext(), "新增考试卷").builder()
            .setOnDialogClickListener { str ->
                var testPaperType = TestPaperType()
                testPaperType.name = str
                testPaperAssignFragment?.refreshData(testPaperType)
            }

    }

}