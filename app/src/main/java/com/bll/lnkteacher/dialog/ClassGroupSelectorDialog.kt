package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupSelectorDialog(val context: Context,private val classGroups: List<ClassGroup>) {

    fun builder(): ClassGroupSelectorDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_selector)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window=dialog.window!!
        val layoutParams=window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(context,400f))/2
        dialog.show()

        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)

        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_classgroup_selector, classGroups)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item=mAdapter.getItem(position)
            item!!.isCheck=!item.isCheck
            mAdapter.notifyItemChanged(position)
        }

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val ids= mutableListOf<Int>()
            for (item in classGroups){
                if (item.isCheck)
                    ids.add(item.classId)
            }
            listener?.onSelect(ids)
            dialog.dismiss()
        }

        return this
    }

    private var listener: OnDialogSelectListener? = null

    fun interface OnDialogSelectListener {
        fun onSelect(ids: List<Int>)
    }

    fun setOnDialogSelectListener(listener: OnDialogSelectListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ClassGroup) {
            helper.setGone(R.id.iv_space,item.state!=1)
            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.cb_check,if(item.isCheck) R.mipmap.icon_check_select else R.mipmap.icon_check_nor)
        }
    }

}