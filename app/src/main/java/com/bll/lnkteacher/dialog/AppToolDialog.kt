package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.manager.AppDaoManager
import com.bll.lnkteacher.mvp.model.AppBean
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.BitmapUtils
import com.bll.lnkteacher.utils.DP2PX
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AppToolDialog(val context: Context) {

    private var dialog:Dialog?=null

    fun builder(): AppToolDialog? {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_app_tool)
        val window=dialog?.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        layoutParams.x=DP2PX.dip2px(context,10f)
        layoutParams.y= DP2PX.dip2px(context,46f)
        dialog?.show()

        val lists=AppDaoManager.getInstance().queryTool()
        val rv_list=dialog?.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = LinearLayoutManager(context)
        val mAdapter = MyAdapter(R.layout.item_app_name_list, lists)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val packageName= lists[position].packageName
            if (packageName.equals(Constants.PACKAGE_GEOMETRY)){
                listener?.onClick()
            }
            else{
                AppUtils.startAPP(context,packageName)
            }
            dismiss()
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

    class MyAdapter(layoutResId: Int, data: List<AppBean>?) : BaseQuickAdapter<AppBean, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: AppBean) {
            helper.setText(R.id.tv_name,item.appName)
            helper.setImageDrawable(R.id.iv_image,BitmapUtils.byteToDrawable(item.imageByte))
        }
    }

    var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick()
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener?) {
        listener = onDialogClickListener
    }

}