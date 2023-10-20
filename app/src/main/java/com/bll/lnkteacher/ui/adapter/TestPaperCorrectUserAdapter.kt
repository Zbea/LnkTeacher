package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrectClass
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class TestPaperCorrectUserAdapter(layoutResId: Int, data: List<TestPaperCorrectClass.UserBean>?) : BaseQuickAdapter<TestPaperCorrectClass.UserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: TestPaperCorrectClass.UserBean) {
        helper.setText(R.id.tv_name,item.name)
        val resId=when(item.status){
            1->{
                R.mipmap.icon_student_select_no
            }
            2->{
                R.mipmap.icon_student_select_end
            }
            else->{
                R.drawable.bg_gray_stroke_10dp_corner
            }
        }
        helper.setBackgroundRes(R.id.ll_content,if (item.isCheck) R.drawable.bg_black_solid_10dp_corner else resId)

        helper.setTextColor(R.id.tv_name,if (item.isCheck) mContext.getColor(R.color.white) else mContext.getColor(R.color.black))
    }

}
