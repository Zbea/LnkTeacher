package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.CalenderItemBean
import com.bll.lnkteacher.utils.GlideUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CalenderMyAdapter(layoutResId: Int, data: List<CalenderItemBean>?) : BaseQuickAdapter<CalenderItemBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CalenderItemBean) {
        helper.apply {
            setText(R.id.cb_check,"  "+item.title)
            setChecked(R.id.cb_check,item.isCheck)
            GlideUtils.setImageRoundUrl(mContext,item.imageUrl,getView(R.id.iv_image),8)
            addOnClickListener(R.id.cb_check)
        }
    }

}
