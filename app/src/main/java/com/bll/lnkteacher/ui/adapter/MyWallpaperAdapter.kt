package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.WallpaperBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MyWallpaperAdapter(layoutResId: Int, data: List<WallpaperBean>?) : BaseQuickAdapter<WallpaperBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: WallpaperBean) {
        helper.apply {
            setText(R.id.cb_check,"  "+item.title)
            setChecked(R.id.cb_check,item.isCheck)
            GlideUtils.setImageRoundUrl(mContext,item.paths[0],getView(R.id.iv_image_left),8)
            GlideUtils.setImageRoundUrl(mContext,item.paths[1],getView(R.id.iv_image_right),8)
            addOnClickListener(R.id.cb_check)
        }
    }

}
