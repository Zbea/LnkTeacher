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
        helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item.date*1000))
        helper.setChecked(R.id.cb_check,item.isCheck)
        helper.setVisible(R.id.cb_check,item.sendType==0)
        helper.setGone(R.id.tv_student_name,item.sendType==2)
        helper.setGone(R.id.rv_list,item.sendType==0)
        helper.setText(R.id.tv_student_name,item.teacherName)

        val classInfos=item.classInfo.split(",")

        val rvList=helper.getView<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext)
        val mAdapter = MyAdapter(R.layout.item_message_classname, classInfos)
        rvList.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)

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
