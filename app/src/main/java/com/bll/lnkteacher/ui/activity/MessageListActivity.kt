package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.MessageSendDialog
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.mvp.presenter.MessagePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.MessageListAdapter
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_message_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class MessageListActivity : BaseActivity(),IContractView.IMessageView {

    private val mPresenter=MessagePresenter(this)
    private var lists = mutableListOf<MessageBean>()
    private var mAdapter: MessageListAdapter? = null
    private var sendDialog: MessageSendDialog? = null

    override fun onList(message: Message) {
        setPageNumber(message.total)
        lists=message.list
        mAdapter?.setNewData(lists)
    }

    override fun onSend() {
        showToast(R.string.toast_send_success)
        //通知首页刷新消息
        EventBus.getDefault().post(MESSAGE_EVENT)
        pageIndex=1
        fetchData()
    }

    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        EventBus.getDefault().post(MESSAGE_EVENT)
        pageIndex=1
        fetchData()
    }


    override fun layoutId(): Int {
        return R.layout.ac_message_list
    }

    override fun initData() {
        pageSize=10
        fetchData()
    }

    override fun initView() {
        setPageTitle("消息通知")
        showView(iv_manager, tv_send)
        iv_manager.setImageResource(R.mipmap.icon_notebook_delete)

        iv_manager.setOnClickListener {
            val datas=mAdapter?.data
            val ids= mutableListOf<Int>()
            for (item in datas!!){
                if (item.isCheck){
                    ids.add(item.id)
                }
            }
            if (ids.size>0){
                val map=HashMap<String,Any>()
                map["ids"]=ids.toIntArray()
                mPresenter.deleteMessages(map)
            }
            else{
                showToast(R.string.toast_selector_message)
            }
        }

        tv_send.setOnClickListener {
            if (sendDialog == null) {
                sendDialog = MessageSendDialog(this).builder()
                sendDialog?.setOnClickListener { contenStr, ids ->
                    val map=HashMap<String,Any>()
                    map["title"]=contenStr
                    map["classIds"]=ids.toIntArray()
                    mPresenter.sendMessage(map)
                }
            } else {
                sendDialog?.show()
            }

        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageListAdapter(R.layout.item_message, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, 40))
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["type"]=1
        mPresenter.getList(map,true)
    }


}