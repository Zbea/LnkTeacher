package com.bll.lnkteacher.ui.adapter

import android.widget.ImageView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AppList
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppCenterListAdapter(layoutResId: Int, data: List<AppList.ListBean>?) : BaseQuickAdapter<AppList.ListBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AppList.ListBean) {
        helper.apply {
            setText(R.id.tv_name,item.nickname)
            setText(R.id.tv_introduce,item.introduction)
            setText(R.id.tv_price,if (item.price==0) "免费" else item.price.toString())
            setGone(R.id.tv_price_title,item.price!=0)
            setText(R.id.btn_download,if (item.buyStatus==0) mContext.getString(R.string.buy) else mContext.getString(R.string.download))
            val image=getView<ImageView>(R.id.iv_image)
            GlideUtils.setImageRoundUrl(mContext,item.assetUrl,image,5)
        }
    }

}
