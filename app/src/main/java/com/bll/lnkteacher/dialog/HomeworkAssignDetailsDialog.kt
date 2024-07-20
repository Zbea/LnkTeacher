package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList.DetailsBean
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class HomeworkAssignDetailsDialog(val mContext: Context, private val items:List<DetailsBean>) {

    private var dialog:Dialog?=null
    private var mAdapter:ListAdapter?=null
    private var position=0
    fun builder(): HomeworkAssignDetailsDialog {

        dialog = Dialog(mContext)
        dialog?.setContentView(R.layout.dialog_homework_assign_details)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x=(Constants.WIDTH- DP2PX.dip2px(mContext,720f))/2
        dialog?.show()

        val rvList= dialog?.findViewById<RecyclerView>(R.id.rv_list)
        mAdapter= ListAdapter(R.layout.item_homework_assign_details, items)
        rvList?.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        rvList?.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_delete){
                this.position=position
                onDialogClickListener?.onDelete(items[position].id)
            }
        }
        return this
    }

    fun show(){
        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }

    fun refreshList(){
        mAdapter?.remove(position)
    }

    var onDialogClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onDelete(id:Int)
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        this.onDialogClickListener = onDialogClickListener
    }

    class ListAdapter(layoutResId: Int, data: List<DetailsBean>) : BaseQuickAdapter<DetailsBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DetailsBean) {
            helper.setText(R.id.tv_homework_type,item.name)
            helper.setText(R.id.tv_message,item.info)
            helper.setText(R.id.tv_date,DateUtils.longToStringDataNoYear(item.time))
            helper.setText(R.id.tv_end_date,DateUtils.longToStringDataNoYear(item.endTime))

            val classNames=item.classInfo.split(",")
            val rvList=helper.getView<RecyclerView>(R.id.rv_list)
            val mAdapter= MyAdapter(R.layout.item_homework_assign_details_classgroup, classNames)
            rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
            rvList.adapter = mAdapter
            mAdapter.bindToRecyclerView(rvList)
            rvList.addItemDecoration(SpaceItemDeco(10))

            helper.addOnClickListener(R.id.iv_delete)

        }


        class MyAdapter(layoutResId: Int, data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

            override fun convert(helper: BaseViewHolder, item: String) {
                helper.setText(R.id.tv_class_name,item)
            }
        }

    }


}