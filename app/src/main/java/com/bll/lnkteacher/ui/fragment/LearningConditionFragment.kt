package com.bll.lnkteacher.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.ui.fragment.learning.LearningExamFragment
import com.bll.lnkteacher.ui.fragment.learning.LearningHomeworkFragment
import com.bll.lnkteacher.ui.fragment.learning.LearningTestPaperFragment

class LearningConditionFragment: BaseFragment() {

    private var homeworkFragment: LearningHomeworkFragment? = null
    private var testPaperFragment: LearningTestPaperFragment? = null
    private var examFragment: LearningExamFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_learning_condition
    }

    override fun initView() {
        setTitle(DataBeanManager.getIndexLeftData()[3].name)

        homeworkFragment = LearningHomeworkFragment()
        testPaperFragment = LearningTestPaperFragment()
        examFragment = LearningExamFragment()

        switchFragment(lastFragment, homeworkFragment)

        initTab()
    }

    override fun lazyLoad() {

    }

    private fun initTab() {
        val strs= arrayOf("作业","测卷","考卷")
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
            0-> {
                switchFragment(lastFragment, homeworkFragment)
            }
            1->{
                switchFragment(lastFragment, testPaperFragment)
            }
            else -> {
                switchFragment(lastFragment,examFragment)
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
                ft?.add(R.id.fl_content_learning, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    override fun onRefreshData() {
        homeworkFragment?.onRefreshData()
        testPaperFragment?.onRefreshData()
        examFragment?.onRefreshData()
    }

}