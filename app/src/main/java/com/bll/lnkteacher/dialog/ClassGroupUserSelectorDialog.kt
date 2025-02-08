package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupUserSelectorDialog(val context: Context, private val users: List<ClassGroupUser>) {

    fun builder(): ClassGroupUserSelectorDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_user_selector)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)


        rv_list?.layoutManager = GridLayoutManager(context,3)
        val mAdapter = MyAdapter(R.layout.item_classgroup_user_selector, users)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list?.addItemDecoration(SpaceGridItemDeco(3,  30))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item=mAdapter.getItem(position)
            item!!.isCheck=!item.isCheck
            mAdapter.notifyItemChanged(position)
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val selectUsers= mutableListOf<ClassGroupUser>()
            for (item in users){
                if (item.isCheck)
                {
                    selectUsers.add(item)
                }
            }
            if (selectUsers.size>0)
                listener?.onSelect(selectUsers)
            dialog.dismiss()
        }

        return this
    }

    private var listener: OnDialogSelectListener? = null

    fun interface OnDialogSelectListener {
        fun onSelect(users: List<ClassGroupUser>)
    }

    fun setOnDialogSelectListener(listener: OnDialogSelectListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
            helper.setText(R.id.tv_name,item.nickname)
            helper.setImageResource(R.id.cb_check,if(item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
        }
    }

}