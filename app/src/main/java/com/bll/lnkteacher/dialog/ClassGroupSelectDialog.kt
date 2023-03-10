package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ClassGroup
import com.bll.lnkteacher.ui.adapter.MainClassGroupAdapter
import com.bll.lnkteacher.widget.SpaceGridItemDeco

/**
 * 班级选择
 */
class ClassGroupSelectDialog(val context: Context){

    fun builder(): ClassGroupSelectDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_class_selector)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        val classs= DataBeanManager.classGroups

        val rvList=dialog.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter = MainClassGroupAdapter(R.layout.item_classgroup, classs)
        rvList.layoutManager = GridLayoutManager(context,2)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(2,40))
        mAdapter.setOnItemClickListener { _, view, position ->
            onClickListener?.onSelect(classs[position])
            dialog.dismiss()
        }

        return this
    }


    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSelect(classGroup: ClassGroup)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }


}