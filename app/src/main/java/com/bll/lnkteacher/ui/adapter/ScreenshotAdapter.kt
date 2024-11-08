package com.bll.lnkteacher.ui.adapter

import android.widget.ImageView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class ScreenshotAdapter(layoutResId: Int, data: List<File>?) : BaseQuickAdapter<File, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, file: File) {
        helper.apply {
            setText(R.id.tv_name,file.name.replace(".png",""))
            val image=getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext,file.path,image,12)
        }
    }

}
