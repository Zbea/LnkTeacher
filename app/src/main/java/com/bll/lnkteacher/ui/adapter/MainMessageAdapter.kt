package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainMessageAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        when(item.sendType){
            1->{
                helper.setGone(R.id.tv_message_source,false)
            }
            2->{
                helper.setGone(R.id.tv_message_source,true)
                helper.setText(R.id.tv_message_source,item.teacherName+"同学：")
            }
            3->{
                helper.setGone(R.id.tv_message_source,true)
                helper.setText(R.id.tv_message_source,"学校通知：")
            }
        }

        helper.setText(R.id.tv_message_content,item.content)
        helper.setText(R.id.tv_message_time,DateUtils.longToStringWeek(item.date))
    }
}
