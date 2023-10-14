package com.bll.lnkteacher.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageListAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {

        helper.setText(R.id.tv_content,item.content)
        helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item.date))
        helper.setChecked(R.id.cb_check,item.isCheck)
        when(item.sendType){
            1->{
                helper.setVisible(R.id.cb_check,true)
                helper.setGone(R.id.rv_list,true)
                helper.setGone(R.id.tv_student_name,false)
                val classInfos=item.classInfo.split(",")
                val rvList=helper.getView<RecyclerView>(R.id.rv_list)
                rvList.layoutManager = LinearLayoutManager(mContext)
                val mAdapter = MyAdapter(R.layout.item_message_classname, classInfos)
                rvList.adapter = mAdapter
                mAdapter.bindToRecyclerView(rvList)
            }
            2->{
                helper.setVisible(R.id.cb_check,false)
                helper.setGone(R.id.rv_list,false)
                helper.setGone(R.id.tv_student_name,true)
                helper.setText(R.id.tv_student_name,item.teacherName+"同学")
            }
            else->{
                helper.setVisible(R.id.cb_check,false)
                helper.setGone(R.id.rv_list,false)
                helper.setGone(R.id.tv_student_name,true)
                helper.setText(R.id.tv_student_name,"学校通知")
            }
        }
        helper.setOnClickListener(R.id.cb_check) {
            item.isCheck = !item.isCheck
            notifyDataSetChanged()
        }
    }


    class MyAdapter(layoutResId: Int,data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.tv_name,item)
        }

    }

}
