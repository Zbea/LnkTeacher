package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.TestPaperType
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperAssignAdapter(layoutResId: Int, data: List<TestPaperType>?) : BaseQuickAdapter<TestPaperType, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TestPaperType) {
        helper.setText(R.id.tv_name,item.name)
    }

}
