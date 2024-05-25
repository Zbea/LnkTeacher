package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupUserSelectorDialog(val context: Context, private val users:MutableList<ClassGroupUser>) {

    fun builder(): ClassGroupUserSelectorDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_user_selector)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val tvOk = dialog.findViewById<TextView>(R.id.tv_ok)

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,4)
        val mAdapter = MyAdapter(R.layout.item_classgroup_user_selector, users)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDeco1(4, 0, 20))
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item=users[position]
            if (view.id==R.id.cb_check){
                item.isCheck=!item.isCheck
                mAdapter.notifyItemChanged(position)
            }
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvOk.setOnClickListener {
            val ids = mutableListOf<Int>()
            for (ite in users) {
                if (ite.isCheck) {
                    ids.add(ite.studentId)
                }
            }
            listener?.onClick(ids)
            dialog.dismiss()
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(ids: List<Int>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
            helper.setText(R.id.tv_name,item.nickname)
            helper.setImageResource(R.id.cb_check,if (item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
            helper.addOnClickListener(R.id.cb_check)
        }
    }

}