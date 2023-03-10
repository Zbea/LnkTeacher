package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.MessageSendDialog
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.ui.adapter.MessageListAdapter
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_message_list.*
import kotlinx.android.synthetic.main.common_title.*

class MessageListActivity : BaseActivity() {

    private var lists = mutableListOf<MessageBean>()
    private var mAdapter: MessageListAdapter? = null
    private var sendDialog: MessageSendDialog? = null

    override fun layoutId(): Int {
        return R.layout.ac_message_list
    }

    override fun initData() {
        lists = DataBeanManager.message
    }

    override fun initView() {
        setPageTitle("消息通知")
        showView(iv_manager, tv_send)
        iv_manager.setImageResource(R.mipmap.icon_notebook_delete)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageListAdapter(R.layout.item_message, lists).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, 40))
            }

        iv_manager.setOnClickListener {

        }

        tv_send.setOnClickListener {
            if (sendDialog == null) {
                sendDialog = MessageSendDialog(this).builder()
                sendDialog?.setOnClickListener { contenStr, classGroups ->

                }
            } else {
                sendDialog?.show()
            }

        }

    }




}