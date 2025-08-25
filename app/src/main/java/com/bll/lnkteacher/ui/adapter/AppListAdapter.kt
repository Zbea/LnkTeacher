package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppListAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppBean) {
        helper.run {
            item.run{
                setText(R.id.tv_name,appName)
                if (item.type==2) {
                    GlideUtils.setImageRoundUrl(mContext,item.imageUrl,getView(R.id.iv_image),10)
                }
                else{
                    setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(imageByte))
                }
                setImageResource(R.id.cb_check,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
            }
            addOnClickListener(R.id.ll_name)
        }
    }

}
