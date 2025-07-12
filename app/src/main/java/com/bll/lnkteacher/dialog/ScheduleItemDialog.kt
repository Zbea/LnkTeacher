package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ScheduleItemDialog(val context: Context, val titleStr: String, val items:MutableList<ItemList>) {

    fun builder(): ScheduleItemDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_item_schedule_plan)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.show()

        val tv_title = dialog.findViewById<TextView>(R.id.tv_title)
        tv_title.text=titleStr
        val iv_close = dialog.findViewById<ImageView>(R.id.iv_close)
        iv_close.setOnClickListener {
            dialog.dismiss()
        }
        val et_custom = dialog.findViewById<EditText>(R.id.et_custom)

        val tv_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        tv_ok.setOnClickListener {
            val contentStr=et_custom.text.toString()
            if (contentStr.isNotEmpty()){
                listener?.onClick(contentStr)
                dialog.dismiss()
            }
        }

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,2)
        val mAdapter = MyAdapter(R.layout.item_schedule_title, items)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDeco2(20, DP2PX.dip2px(context,15f)))
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(items[position].name)
            dialog.dismiss()
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(contentStr:String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name,item.name)
        }
    }

}