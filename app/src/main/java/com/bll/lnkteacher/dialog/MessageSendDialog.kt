package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class MessageSendDialog(private val context: Context) {

    private var dialog: Dialog?=null
    private var ids= mutableListOf<Int>()

    fun builder(): MessageSendDialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_message_send)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val tvOK = dialog?.findViewById<TextView>(R.id.tv_ok)
        val tvCancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val et_content = dialog?.findViewById<EditText>(R.id.et_content)
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)

        val groups=DataBeanManager.classGroups
        val mAdapter=MyAdapter(R.layout.item_message_classgroup,groups)
        rvList?.layoutManager=GridLayoutManager(context,3)
        rvList?.adapter=mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(3,  30))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item=groups[position]
            item.isCheck=!item.isCheck
            mAdapter.notifyDataSetChanged()
        }

        tvCancel?.setOnClickListener { dismiss() }
        tvOK?.setOnClickListener {
            val contentStr=et_content?.text.toString()
            for (item in mAdapter.data){
                if (item.isCheck){
                    ids.add(item.classId)
                }
            }

            if (contentStr.isNotEmpty()&&ids.size>0)
            {
                dismiss()
                listener?.onSend(contentStr,ids)
            }
        }

        return this
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onSend(contentStr:String,ids:List<Int>)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

     class MyAdapter(layoutResId:Int,classs:MutableList<ClassGroup>):BaseQuickAdapter<ClassGroup,BaseViewHolder>(layoutResId,classs){
         override fun convert(helper: BaseViewHolder, item: ClassGroup?) {
             helper.setText(R.id.tv_class_name,item?.name)
             helper.setChecked(R.id.cb_check,item?.isCheck!!)
         }
     }

}