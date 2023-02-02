package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MessageClassGroupDialog(val context: Context) {

    private var dialog:Dialog?=null

    fun builder(): MessageClassGroupDialog? {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_message_classgroup_selector)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        var mAdapter= MyAdapter(R.layout.item_message_classgroup, DataBeanManager.getInstance().classGroups)
        rvList?.layoutManager = LinearLayoutManager(context)//创建布局管理
        rvList?.adapter = mAdapter
        rvList?.addItemDecoration(SpaceItemDeco(0,0,0,20,0))
        mAdapter?.bindToRecyclerView(rvList)

        dialog?.setOnDismissListener {
            val data=mAdapter?.data
            val groups= mutableListOf<ClassGroup>()
            for (item in data){
                if (item.isCheck)
                    groups.add(item)
            }
            listener?.onClick(groups)
            dismiss()
        }

        return this
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    class MyAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ClassGroup) {
            val cb_class=helper.getView<CheckBox>(R.id.cb_check)
            cb_class.text=item.name
            cb_class.setOnCheckedChangeListener { compoundButton, b ->
                item.isCheck=b
            }
        }

    }



    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(groups: List<ClassGroup>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }



}