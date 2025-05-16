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
            if (item.assignItem==null){
                setText(R.id.tv_layout_time,"自动布置时间")
                setTextColor(R.id.tv_layout_time,mContext.getColor(R.color.black))
            }
            else{
                setTextColor(R.id.tv_layout_time,if (item.assignItem.taskState==2) mContext.getColor(R.color.black) else mContext.getColor(R.color.black_90))
                setText(R.id.tv_layout_time,DateUtils.longToStringNoYear1(item.assignItem.assignTime))
            }
            setGone(R.id.tv_answer,!item.answerUrl.isNullOrEmpty())
            if (getView<ImageView>(R.id.iv_image)!=null){
                GlideUtils.setImageRoundUrl(mContext,item.url,getView(R.id.iv_image),8)
                addOnClickListener(R.id.cb_check,R.id.tv_answer,R.id.iv_image,R.id.tv_layout_time)
                addOnLongClickListener(R.id.iv_image,R.id.tv_layout_time)
            }
            else{
                addOnLongClickListener(R.id.cb_check,R.id.tv_layout_time)
                addOnClickListener(R.id.cb_check,R.id.tv_answer,R.id.tv_layout_time)
            }
        }
    }
}
