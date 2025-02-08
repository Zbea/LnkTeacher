package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class MessageSendDialog(private val context: Context) {

    private var dialog: Dialog?=null
    private var mAdapter:MyAdapter?=null
    private var classGroups= mutableListOf<ClassGroup>()
    private var position=0

    fun builder(): MessageSendDialog {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_message_send)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()

        val tvOK = dialog?.findViewById<TextView>(R.id.tv_ok)
        val tvCancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val et_content = dialog?.findViewById<EditText>(R.id.et_content)
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)

        for (item in DataBeanManager.classGroups){
            if (item.studentCount>0&&item.state==1){
                classGroups.add(item)
            }
        }
        mAdapter=MyAdapter(R.layout.item_message_classgroup,classGroups)
        rvList?.layoutManager=LinearLayoutManager(context)
        rvList?.adapter=mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceItemDeco(20))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            listener?.onClassGroup(classGroups[position])
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.cb_check){
                val item=classGroups[position]
                item.isCheck=!item.isCheck
                if (item.isCheck)
                    item.classUsers.clear()
                mAdapter?.notifyItemChanged(position)
            }
        }

        tvCancel?.setOnClickListener { dismiss() }
        tvOK?.setOnClickListener {
            val contentStr=et_content?.text.toString()

            val classIds= mutableListOf<Int>()
            val studentIds= mutableListOf<Int>()
            for (item in classGroups){
                if (item.isCheck){
                    classIds.add(item.classId)
                }
                if (item.classUsers.isNotEmpty()){
                    for (user in item.classUsers){
                        studentIds.add(user.studentId)
                    }
                }
            }

            if (contentStr.isNotEmpty())
            {
                if (classIds.isNotEmpty()||studentIds.isNotEmpty()){
                    dismiss()
                    listener?.onSend(contentStr,classIds,studentIds)
                }
            }
        }

        return this
    }

    fun setClassUser(classUsers:List<ClassGroupUser>){
        val item=classGroups[position]
        for (user in classUsers){
            for (selectUser in item.classUsers){
                if (selectUser.studentId==user.studentId)
                    user.isCheck=true
            }
        }
        ClassGroupUserSelectorDialog(context,classUsers).builder().setOnDialogSelectListener{
            item.classUsers=it
            if (item.isCheck)
                item.isCheck=false
            mAdapter?.notifyItemChanged(position)
        }
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    private var listener: OnClickListener? = null

    interface OnClickListener {
        fun onSend(contentStr:String,classIds:List<Int>,studentIds:List<Int>)
        fun onClassGroup(classGroup:ClassGroup)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

     class MyAdapter(layoutResId:Int,classGroups:MutableList<ClassGroup>):BaseQuickAdapter<ClassGroup,BaseViewHolder>(layoutResId,classGroups){
         override fun convert(helper: BaseViewHolder, item: ClassGroup?) {
             helper.setText(R.id.tv_class_name,item?.name)
             helper.setChecked(R.id.cb_check,item?.isCheck!!)
             var classUserStr=""
             val classUsers=item.classUsers
             for (user in classUsers){
                 classUserStr += if (classUsers.indexOf(user)==classUsers.size-1){
                     user.nickname
                 } else{
                     user.nickname+"、"
                 }
             }
             helper.setText(R.id.tv_user_name,classUserStr)
             helper.addOnClickListener(R.id.cb_check)
         }
     }

}