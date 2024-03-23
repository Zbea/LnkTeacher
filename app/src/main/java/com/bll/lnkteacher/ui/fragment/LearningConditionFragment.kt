package com.bll.lnkteacher.ui.fragment

import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment

class LearningConditionFragment:BaseMainFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_learning_condition
    }

    override fun initView() {
        setTitle("学情")
    }

    override fun lazyLoad() {
    }
}