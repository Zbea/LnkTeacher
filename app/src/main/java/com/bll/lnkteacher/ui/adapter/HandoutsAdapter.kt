package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.HandoutDaoManager
import com.bll.lnkteacher.mvp.model.HandoutBean
import com.bll.lnkteacher.utils.FileUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class HandoutsAdapter(layoutResId: Int, data: List<HandoutBean>?) : BaseQuickAdapter<HandoutBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HandoutBean) {
        helper.apply {
            setText(R.id.tv_name,item.title)
            setGone(R.id.iv_download, HandoutDaoManager.getInstance().isExist(item.id))
            when(FileUtils.getUrlFormat(item.bookPath)){
                ".ppt",".pptx"->{
                    setImageResource(R.id.iv_image,R.mipmap.icon_file_ppt)
                }
                ".pdf"->{
                    setImageResource(R.id.iv_image,R.mipmap.icon_file_document)
                }
            }
        }
    }

}
