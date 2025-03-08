package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.WallpaperBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class WallpaperAdapter(layoutResId: Int, data: List<WallpaperBean>?) : BaseQuickAdapter<WallpaperBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: WallpaperBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            GlideUtils.setImageRoundUrl(mContext,item.bodyUrl.split(",")[0],getView(R.id.iv_image_left),8)
            GlideUtils.setImageRoundUrl(mContext,item.bodyUrl.split(",")[0],getView(R.id.iv_image_right),8)
            setText(R.id.tv_price,if (item.price==0) "免费" else item.price.toString()+"学豆")
            setText(R.id.btn_download,if (item.buyStatus==1) "下载" else "购买")
            addOnClickListener(R.id.btn_download)
        }

    }

}
