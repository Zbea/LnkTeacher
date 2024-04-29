package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

class MessageListActivity : BaseActivity(),IContractView.IMessageView {

    private lateinit var mPresenter:MessagePresenter
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
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        pageSize=12
        fetchData()
    }

    override fun initChangeScreenData() {
        mPresenter=MessagePresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("消息通知")
        showView(iv_manager, iv_setting)
        iv_setting.setImageResource(R.mipmap.icon_delete)
        iv_manager.setImageResource(R.mipmap.icon_save)

        iv_setting.setOnClickListener {
            val ids= mutableListOf<Int>()
            for (item in lists){
                if (item.isCheck){
                    ids.add(item.id)
                }
            }
            if (ids.size>0){
                val map=HashMap<String,Any>()
                map["ids"]=ids.toIntArray()
                mPresenter.deleteMessages(map)
            }
        }

        iv_manager.setOnClickListener {
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
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f),
            DP2PX.dip2px(this,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageListAdapter(R.layout.item_message, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.cb_check){
                    lists[position].isCheck=!lists[position].isCheck
                    notifyItemChanged(position)
                }
            }
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