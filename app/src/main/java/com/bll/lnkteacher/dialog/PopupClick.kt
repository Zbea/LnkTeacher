package com.bll.lnkteacher.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.PopupBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PopupClick(val context:Context, val list:MutableList<PopupBean>, val view: View, val width:Int) {

    private var mPopupWindow:PopupWindow?=null
    private var xoff=0

    constructor(context: Context, list: MutableList<PopupBean>, view: View):this(context, list, view, 0)

    fun builder(): PopupClick?{
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_list, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView=popView
            isFocusable=true // 设置PopupWindow可获得焦点
            isTouchable=true // 设置PopupWindow可触摸
            isOutsideTouchable=true // 设置非PopupWindow区域可触摸
            if (this@PopupClick.width!=0){
                width=this@PopupClick.width
            }

        }

        var rvList=popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        var mAdapter = MAdapter(R.layout.item_popwindow_list, list)
        rvList.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            onSelectListener?.onSelect(list[position])
            dismiss()
        }

        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        xoff = mPopupWindow?.contentView?.measuredWidth!!

        show()
        return this
    }

    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view,if (width!=0)0 else -xoff, 5,Gravity.RIGHT)
        }
    }

   private var onSelectListener:OnSelectListener?=null

    fun setOnSelectListener(onSelectListener:OnSelectListener)
    {
        this.onSelectListener=onSelectListener
    }

    fun interface OnSelectListener{
        fun onSelect(item: PopupBean)
    }


    private class MAdapter(layoutResId: Int, data: List<PopupBean>?) : BaseQuickAdapter<PopupBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: PopupBean) {

            helper.setText(R.id.tv_name,item.name)
            helper.setImageResource(R.id.iv_check,item.resId)

        }

    }

}