package com.bll.lnkteacher.ui.fragment

import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment

class LearningConditionFragment: BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_learning_condition
    }

    override fun initView() {
        setTitle("教情")
    }

    override fun lazyLoad() {
    }
}