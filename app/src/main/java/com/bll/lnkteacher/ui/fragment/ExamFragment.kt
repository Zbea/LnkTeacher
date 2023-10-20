package com.bll.lnkteacher.ui.fragment

import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment

class ExamFragment:BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_group_manager
    }

    override fun initView() {
        setTitle(R.string.main_exam_title)
    }

    override fun lazyLoad() {
    }
}