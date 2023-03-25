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
        setTitle(R.string.main_bookcase_title)
    }

    override fun lazyLoad() {
    }


}