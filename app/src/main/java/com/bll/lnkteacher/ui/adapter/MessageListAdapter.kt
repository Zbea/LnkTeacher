package com.bll.lnkteacher.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageListAdapter(layoutResId: Int, data: MutableList<MessageBean>?) : BaseQuickAdapter<MessageBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MessageBean) {

        helper.setText(R.id.tv_content,item.content)
        helper.setText(R.id.tv_date,DateUtils.longToStringWeek(item.createTime))
        helper.setChecked(R.id.cb_check,item.isCheck)

        var rvList=helper.getView<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(mContext)
        var mAdapter = MyAdapter(R.layout.item_message_classname, item.classGroups)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)


    }


    class MyAdapter(layoutResId: Int,data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ClassGroup) {
            helper.setText(R.id.tv_name,item.name)
        }

    }

}
