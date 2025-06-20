package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ExamCorrectUserAdapter(layoutResId: Int, data: List<ExamClassUserList.ClassUserBean>?) : BaseQuickAdapter<ExamClassUserList.ClassUserBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: ExamClassUserList.ClassUserBean) {
        helper.setText(R.id.tv_name,item.studentName)
        helper.setText(R.id.tv_score,if (item.status==2) item.score.toString() else "")
        helper.setGone(R.id.iv_share,item.shareType==1)

        var colorRes=0
        var bgRes=0
        var shareResId=0

        if (item.isCheck){
            colorRes=mContext.getColor(R.color.white)
            bgRes=R.drawable.bg_black_solid_5dp_corner
            shareResId=R.mipmap.icon_share_type_white
        }
        else{
            shareResId=R.mipmap.icon_share_type_black
            if (item.status==3){
                colorRes=mContext.getColor(R.color.gray)
                bgRes=R.drawable.bg_gray_stroke_5dp_corner
            }
            else{
                colorRes=mContext.getColor(R.color.black)
                bgRes=R.drawable.bg_black_stroke_5dp_corner
            }
        }
        helper.setTextColor(R.id.tv_name,colorRes)
        helper.setBackgroundRes(R.id.tv_name,bgRes)
        helper.setImageResource(R.id.iv_share,shareResId)
    }

}
