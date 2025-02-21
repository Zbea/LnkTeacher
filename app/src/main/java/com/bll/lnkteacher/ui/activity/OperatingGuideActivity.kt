package com.bll.lnkteacher.ui.activity

import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity

class OperatingGuideActivity :BaseActivity() {
    override fun layoutId(): Int {
        return R.layout.ac_list_tab
    }

    override fun initData() {
    }

    override fun initView() {
        setPageTitle("操作手册")
    }
}