package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamCorrectUserAdapter(layoutResId: Int, data: List<ExamClassUserList.ClassUserBean>?) : BaseQuickAdapter<ExamClassUserList.ClassUserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ExamClassUserList.ClassUserBean) {
        helper.setText(R.id.tv_name,item.studentName)
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
        helper.setBackgroundRes(R.id.tv_name,if (item.isCheck) R.drawable.bg_black_solid_10dp_corner else resId)

        helper.setTextColor(R.id.tv_name,if (item.isCheck) mContext.getColor(R.color.white) else mContext.getColor(R.color.black))
    }

}
