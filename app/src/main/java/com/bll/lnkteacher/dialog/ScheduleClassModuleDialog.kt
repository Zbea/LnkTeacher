package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ModuleBean
import com.bll.lnkteacher.widget.SpaceGridItemDeco2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class ScheduleClassModuleDialog(private val context: Context,val list:MutableList<ModuleBean>) {

    fun builder(): ScheduleClassModuleDialog {
        val dialog= Dialog(context)
        dialog.setContentView(R.layout.dialog_schedule_course_module)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel?.setOnClickListener { dialog.dismiss() }

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rvList?.layoutManager = GridLayoutManager(context,2)
        val mAdapter = MAdapter(R.layout.item_module, list)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco2(20,30))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(position)
            dialog.dismiss()
        }

        return this
    }


    private class MAdapter(layoutResId: Int, data: List<ModuleBean>?) : BaseQuickAdapter<ModuleBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ModuleBean) {
            helper.setText(R.id.tv_name,item.name)
            val ivImage=helper.getView<ImageView>(R.id.iv_image)
            ivImage.layoutParams=LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
            ivImage.setImageResource(item.resId)
        }

    }


    private var listener: OnClickListener? = null

    fun interface OnClickListener {
        fun onClick(type:Int)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

}