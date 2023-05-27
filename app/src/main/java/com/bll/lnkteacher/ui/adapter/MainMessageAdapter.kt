package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainMessageAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        val dateStr=if (item.sendType==2)"发送学生:"+item.teacherName+"   时间:"+DateUtils.longToStringWeek(item.date) else DateUtils.longToStringWeek(item.date)
        helper.setText(R.id.tv_message_content,item.content)
        helper.setText(R.id.tv_message_time,dateStr)
    }
}
