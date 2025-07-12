package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupPermissionDialog(val context: Context) {
    var time=0

    fun builder(): ClassGroupPermissionDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_permission)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val iv_close = dialog.findViewById<ImageView>(R.id.iv_close)
        iv_close.setOnClickListener {
            dialog.dismiss()
        }
        val iv_tips = dialog.findViewById<ImageView>(R.id.iv_tips)
        iv_tips.setOnClickListener {
            ClassGroupPermissionTipsDialog(context,iv_tips).builder()
        }

        val et_custom = dialog.findViewById<EditText>(R.id.et_custom)

        val tv_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        tv_ok.setOnClickListener {
            val contentStr=et_custom.text.toString()
            if (contentStr.isNotEmpty()){
                time=contentStr.toInt()
            }
            if (time>0){
                listener?.onClick(time)
                dialog.dismiss()
            }
        }

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,3)
        val mAdapter = MyAdapter(R.layout.item_schedule_title, DataBeanManager.times)
        rv_list?.adapter = mAdapter
        rv_list?.addItemDecoration(SpaceGridItemDeco1(3,20, DP2PX.dip2px(context,15f)))
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item=mAdapter.data[position]
            for (ite in mAdapter.data){
                ite.isCheck=false
            }
            item.isCheck=true
            mAdapter.notifyDataSetChanged()
            time=item.id
        }

        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(time:Int)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    class MyAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name,item.name)
            val tvName=helper.getView<TextView>(R.id.tv_name)
            tvName.setTextColor(mContext.getColor(if (item.isCheck)R.color.white else R.color.black))
            tvName.setBackgroundResource(if (item.isCheck) R.drawable.bg_black_solid_0dp_corner else R.drawable.bg_black_stroke_0dp_corner)
        }
    }

}