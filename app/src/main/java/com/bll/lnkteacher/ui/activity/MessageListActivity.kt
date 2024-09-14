package com.bll.lnkteacher.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants.Companion.MESSAGE_EVENT
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.MessageSendDialog
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.mvp.presenter.MessagePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.MessageListAdapter
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.iv_manager
import kotlinx.android.synthetic.main.common_title.tv_btn_1
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
        showView(tv_btn_1)
        iv_manager.setImageResource(R.mipmap.icon_save)

        tv_btn_1.text="发送"
        tv_btn_1.setOnClickListener {
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
        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            CommonDialog(this).setContent("确定删除？").builder().onDialogClickListener= object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    val item=lists[position]
                val map=HashMap<String,Any>()
                map["ids"]= mutableListOf(item.id)
                mPresenter.deleteMessages(map)
                }
            }
            true
        }
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["type"]=1
        mPresenter.getList(map,true)
    }

    override fun onRefreshData() {
        fetchData()
    }
}