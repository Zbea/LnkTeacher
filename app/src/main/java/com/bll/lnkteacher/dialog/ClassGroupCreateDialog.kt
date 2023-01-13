package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ListBean
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.KeyboardUtils

class ClassGroupCreateDialog(val context: Context) {


    fun builder(): ClassGroupCreateDialog? {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_classgroup_create)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
//        val sp_province=dialog?.findViewById<Spinner>(R.id.sp_province)
////        sp_province.dropDownVerticalOffset=DP2PX.dip2px(context,40f)
//        sp_province.dropDownWidth=DP2PX.dip2px(context,115f)
//        sp_province.setPopupBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
//        val mAdapter=SpinnerAdapter(context,provinces)
//        sp_province.adapter=mAdapter
        val et_name = dialog.findViewById<EditText>(R.id.et_name)
        val btn_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val btn_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            val name=et_name.text.toString()
            if (!name.isNullOrEmpty())
            {
                dialog.dismiss()
                listener?.onClick(name)
            }
        }

        dialog.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }
        return this
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(str: String)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }


    class SpinnerAdapter(val context: Context, val list: List<ListBean>) : BaseAdapter() {

        override fun getCount(): Int {
            return list.size
        }
        override fun getItem(position: Int): Any {
            return list[position]
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val view=LayoutInflater.from(context).inflate(R.layout.item_dropdown,null)
            val tvName=view.findViewById<TextView>(R.id.tv_name)
            tvName.text=list[position].name
            tvName.setSingleLine()
            tvName.ellipsize=TextUtils.TruncateAt.END
            tvName.height=DP2PX.dip2px(context,40f)
            return view
        }
    }


}