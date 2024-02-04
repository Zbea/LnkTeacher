package com.bll.lnkteacher.ui.adapter

import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageListAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {
        helper.setText(R.id.tv_content,item.content)
        helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item.date))
        helper.setImageResource(R.id.cb_check,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
        when(item.sendType){
            1->{
                helper.setVisible(R.id.cb_check,true)
                helper.setText(R.id.tv_student_name,item.classInfo)
            }
            2->{
                helper.setVisible(R.id.cb_check,false)
                helper.setText(R.id.tv_student_name,item.teacherName+"同学")
            }
            else->{
                helper.setVisible(R.id.cb_check,false)
                helper.setText(R.id.tv_student_name,"学校通知")
            }
        }
    }
}
