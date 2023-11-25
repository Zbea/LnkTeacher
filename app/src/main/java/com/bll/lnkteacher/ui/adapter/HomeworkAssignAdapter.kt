package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignAdapter(layoutResId: Int, data: List<TypeBean>?) : BaseQuickAdapter<TypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TypeBean) {
        if (item.subType==4){
            helper.setBackgroundRes(R.id.iv_image,R.drawable.bg_black_stroke_10dp_corner)
            GlideUtils.setImageRoundUrl(mContext,item.bgResId,helper.getView(R.id.iv_image),10)
        }
        else{
            helper.setText(R.id.tv_name,item.name)
            val bg=if (item.bgResId.isNullOrEmpty()) DataBeanManager.getHomeworkCoverStr() else item.bgResId
            helper.setImageResource(R.id.iv_image,ToolUtils.getImageResId(mContext,bg))
            helper.setBackgroundRes(R.id.iv_image,R.color.color_transparent)
        }
    }

}
