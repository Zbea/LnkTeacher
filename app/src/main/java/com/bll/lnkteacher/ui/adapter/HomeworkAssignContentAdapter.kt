package com.bll.lnkteacher.ui.adapter

import android.widget.ImageView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignContentAdapter(layoutResId: Int, data: List<AssignPaperContentList.AssignPaperContentBean>?) : BaseQuickAdapter<AssignPaperContentList.AssignPaperContentBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AssignPaperContentList.AssignPaperContentBean) {
        helper.apply {
            setChecked(R.id.cb_check,item.isCheck)
            setText(R.id.cb_check,"  "+item.title)
            setText(R.id.tv_standard_time,if (item.standardTime>0) "${item.standardTime}分钟" else "")
            setGone(R.id.ll_date_preset,DateUtils.date10ToDate13(item.time)>System.currentTimeMillis())
            setText(R.id.tv_date_preset,DateUtils.longToStringWeek(item.time))
            setGone(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            if (getView<ImageView>(R.id.iv_image)!=null){
                GlideUtils.setImageUrl(mContext,item.url,getView(R.id.iv_image))
                addOnClickListener(R.id.cb_check,R.id.tv_answer,R.id.iv_image)
                addOnLongClickListener(R.id.iv_image)
            }
            else{
                addOnLongClickListener(R.id.cb_check)
                addOnClickListener(R.id.cb_check,R.id.tv_answer)
            }
        }
    }
}
