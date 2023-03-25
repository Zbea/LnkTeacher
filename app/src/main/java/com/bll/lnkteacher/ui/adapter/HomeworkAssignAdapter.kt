package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.*

class HomeworkAssignAdapter(layoutResId: Int, data: List<TypeBean>?) : BaseQuickAdapter<TypeBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TypeBean) {
        helper.setText(R.id.tv_name,item.name)
        val covers=DataBeanManager.homeworkCover
        val index= Random().nextInt(covers.size)
        helper.setImageResource(R.id.iv_image,covers[index].resId)
    }

}
