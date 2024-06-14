package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageListAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        helper.setText(R.id.tv_message_content,item.content)
        helper.setText(R.id.tv_message_time,DateUtils.longToStringWeek2(item.date))
        when(item.sendType){
            1->{
                helper.setText(R.id.tv_message_name,"发送："+item.classInfo)
            }
            2->{
                helper.setText(R.id.tv_message_name,"来自："+item.teacherName+"同学")
            }
            5 -> {
                helper.setText(R.id.tv_message_name,"年级通知")
            }
            else->{
                helper.setText(R.id.tv_message_name,"学校通知")
            }
        }
    }
}
