package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.testpaper.AnalyseItem.UserBean
import com.bll.lnkteacher.ui.adapter.ExamAnalyseTeachingAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco

class AnalyseUserDetailsDialog(val context: Context, private val students:MutableList<UserBean>) {

    fun builder(): AnalyseUserDetailsDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_analyse_user_details)
        val window=dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams?.width=DP2PX.dip2px(context,700f)
        layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams?.x=DP2PX.dip2px(context,1021-700f)/2
        dialog.show()

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list.layoutManager = GridLayoutManager(context,3)//创建布局管理
        val mAdapter = ExamAnalyseTeachingAdapter(R.layout.item_exam_analyse_teaching_user, students)
        rv_list.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(3,20))

        return this
    }


}