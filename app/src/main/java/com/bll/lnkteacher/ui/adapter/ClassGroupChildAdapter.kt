package com.bll.lnkteacher.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ClassGroupChildAdapter(layoutResId: Int,  data: List<ClassGroup>?) : BaseQuickAdapter<ClassGroup, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ClassGroup) {
        helper.apply {
            setText(R.id.tv_name,item.name)

            val recyclerView=getView<RecyclerView>(R.id.rv_list)
            recyclerView?.layoutManager = GridLayoutManager(mContext,4)
            val mAdapter = UserAdapter(R.layout.item_classgroup_child_user, item.students)
            recyclerView?.adapter = mAdapter
            mAdapter.bindToRecyclerView(recyclerView)
            mAdapter.setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.tv_set){
                    listener?.onClick(adapterPosition,view,item.students[position].studentId)
                }
            }
            addOnClickListener(R.id.tv_add,R.id.tv_edit,R.id.tv_dissolve)
        }
    }

    private var listener: OnItemChildClickListener? = null

    fun interface OnItemChildClickListener {
        fun onClick(position:Int,view:View,studentId: Int)
    }

    fun setCustomItemChildClickListener(listener: OnItemChildClickListener?) {
        this.listener = listener
    }

    class UserAdapter(layoutResId: Int,  data: List<ClassGroupUser>?) : BaseQuickAdapter<ClassGroupUser, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ClassGroupUser) {
            helper.apply {
                setText(R.id.tv_name,item.nickname)
                addOnClickListener(R.id.tv_set)
            }
        }
    }


}
