package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainMessageAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        var typeNameStr=""
        helper.apply {
            when (item.sendType) {
                1 -> {
                    typeNameStr = item.classInfo
                }
                2 -> {
                    typeNameStr = item.teacherName
                }
                3 -> {
                    typeNameStr = "学校通知"
                }
            }
            setText(R.id.tv_message_name, typeNameStr)
            setText(R.id.tv_message_time, DateUtils.longToStringWeek(item.date))
            setText(R.id.tv_message_content,item.content)
        }
    }
}
