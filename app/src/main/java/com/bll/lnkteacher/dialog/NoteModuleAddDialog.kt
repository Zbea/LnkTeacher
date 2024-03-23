package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ModuleBean
import com.bll.lnkteacher.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class NoteModuleAddDialog(private val context: Context, val screenPos:Int,private val type: Int) {

    private var dialog:Dialog?=null

    fun builder(): NoteModuleAddDialog? {
        dialog= Dialog(context)
        dialog?.setContentView(R.layout.dialog_note_add_module)
        val width=if (type==0) DP2PX.dip2px(context,450f) else DP2PX.dip2px(context,611f)
        val window = dialog?.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.width=width
        if (screenPos==3){
            layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams.x=(Constants.WIDTH- width)/2
        }
        dialog?.show()

        val iv_cancel = dialog?.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel?.setOnClickListener { dialog?.dismiss() }

        val datas=if (type==0) DataBeanManager.noteModuleDiary else DataBeanManager.noteModuleBook
        val rvList=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rvList?.layoutManager =GridLayoutManager(context,if (type==0) 2 else 3)//创建布局管理
        val mAdapter = MyAdapter(R.layout.item_note_module, datas)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            dismiss()
            listener?.onClick(datas[position])
        }

        return this
    }


    fun dismiss(){
        if(dialog!=null)
            dialog?.dismiss()
    }

    fun show(){
        if(dialog!=null)
            dialog?.show()
    }


    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(moduleBean: ModuleBean)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    private class MyAdapter(layoutResId: Int, data: List<ModuleBean>?) : BaseQuickAdapter<ModuleBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ModuleBean) {

            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.iv_image,item.resId)

        }

    }

}