package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HomeworkAssignAdapter(layoutResId: Int, data: List<TypeBean>?) : BaseQuickAdapter<TypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TypeBean) {
        if (item.subType==4){
            helper.setText(R.id.tv_name,"")
            GlideUtils.setImageRoundUrl(mContext,item.bgResId,helper.getView(R.id.iv_image),15)
            helper.setBackgroundRes(R.id.rl_bg,R.drawable.bg_black_stroke_10dp_corner)
        }
        else{
            helper.setText(R.id.tv_name,item.name)
            val bg=when(item.subType){
                1->{
                    R.mipmap.icon_homework_cover_3
                }
                3->{
                    R.mipmap.icon_homework_cover_2
                }
                6->{
                    R.mipmap.icon_homework_cover_6
                }
                7->{
                    R.mipmap.icon_homework_cover_7
                }
                8->{
                    R.mipmap.icon_homework_cover_8
                }
                10->{
                    R.mipmap.icon_homework_cover_10
                }
                else->{
                    if (item.name=="作文作业本")  R.mipmap.icon_homework_cover_5 else R.mipmap.icon_homework_cover_1
                }
            }
            helper.setImageResource(R.id.iv_image,bg)
            helper.setBackgroundRes(R.id.rl_bg,R.color.color_transparent)
        }
    }

}
