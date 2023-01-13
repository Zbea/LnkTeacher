package com.bll.lnkteacher.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.MessageClassGroupDialog
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.mvp.model.MessageBean
import com.bll.lnkteacher.ui.adapter.MessageListAdapter
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_message_list.*

class MessageListActivity : BaseActivity() {

    private var lists = mutableListOf<MessageBean>()
    private var mAdapter: MessageListAdapter? = null
    private var dialog: MessageClassGroupDialog? = null

    override fun layoutId(): Int {
        return R.layout.ac_message_list
    }

    override fun initData() {
        setPageTitle("消息通知")
        lists = DataBeanManager.getIncetance().message
    }

    override fun initView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MessageListAdapter(R.layout.item_message, lists)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, 40, 0))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete) {
                deleteView()
            }
        }

        cb_check.setOnCheckedChangeListener { compoundButton, b ->
            for (item in lists) {
                item.isCheck = b
            }
            mAdapter?.notifyDataSetChanged()
        }

        iv_delete.setOnClickListener {
            var it = lists.iterator()
            while (it?.hasNext()) {
                if (it?.next().isCheck) {
                    it?.remove()
                }
            }
            mAdapter?.notifyDataSetChanged()
        }

        tv_class_name.setOnClickListener {
            showClassGroupSelector()
        }

    }

    /**
     * 删除
     */
    private fun deleteView() {
        CommonDialog(this).setContent("确定删除此条通知？").builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }

                override fun ok() {

                }
            })
    }

    /**
     * 班级选择
     */
    private fun showClassGroupSelector() {
        if (dialog == null) {
            dialog = MessageClassGroupDialog(this).builder()
            dialog?.setOnDialogClickListener(object :
                MessageClassGroupDialog.OnDialogClickListener {
                override fun onClick(groups: List<ClassGroup>) {

                }
            })
        } else {
            dialog?.show()
        }

    }

}