package com.bll.lnkteacher.ui.fragment

import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment

/**
 * 书架
 */
class BookCaseFragment: BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle("书架")
    }

    override fun lazyLoad() {
    }


}