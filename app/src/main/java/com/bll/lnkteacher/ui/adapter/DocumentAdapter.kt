package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class DocumentAdapter(layoutResId: Int, data: List<File>?) : BaseQuickAdapter<File, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, file: File) {
        helper.setText(R.id.tv_name,file.name)
        helper.setImageResource(R.id.iv_image,R.mipmap.icon_file_ppt)
    }

}
