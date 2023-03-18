package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrectClass
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectUserAdapter(layoutResId: Int, data: List<TestPaperCorrectClass.UserBean>?) : BaseQuickAdapter<TestPaperCorrectClass.UserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: TestPaperCorrectClass.UserBean) {
        helper.setBackgroundRes(R.id.ll_content,if (item.isCheck) R.drawable.bg_black_solid_5dp_corner else R.drawable.bg_gray_stroke_5dp_corner)
        helper.setText(R.id.tv_name,item.name)
        helper.setTextColor(R.id.tv_name,if (item.isCheck) mContext.getColor(R.color.white) else mContext.getColor(R.color.black))
    }

}
