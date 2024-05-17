package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NumberDialog(val context: Context) {

    private var list= mutableListOf<ItemList>()

    fun builder(): NumberDialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_score_number)
        val window=dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams =window.attributes
        layoutParams.width=Constants.WIDTH
        layoutParams?.gravity = Gravity.TOP or Gravity.END
        layoutParams?.y=DP2PX.dip2px(context,38f)
        dialog.show()

        for (i in 0..49){
            list.add(ItemList(i,i.toString()))
        }

        val rv_list=dialog.findViewById<RecyclerView>(R.id.rv_list)
        rv_list?.layoutManager = GridLayoutManager(context,12)
        val mAdapter = MyAdapter(R.layout.item_number, list)
        rv_list?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(12,20))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            listener?.onClick(list[position].id)
            dialog.dismiss()
        }

        val rg_group=dialog.findViewById<RadioGroup>(R.id.rg_group)
        rg_group?.setOnCheckedChangeListener { radioGroup, id ->
            when(id){
                R.id.rb_0->{
                    list.clear()
                    for (i in 0..49){
                        list.add(ItemList(i,i.toString()))
                    }
                    mAdapter.setNewData(list)
                }
                R.id.rb_1->{
                    list.clear()
                    for (i in 50..99){
                        list.add(ItemList(i,i.toString()))
                    }
                    mAdapter.setNewData(list)
                }
                R.id.rb_2->{
                    list.clear()
                    for (i in 100..150){
                        list.add(ItemList(i,i.toString()))
                    }
                    mAdapter.setNewData(list)
                }
            }
        }

        val ivClose=dialog.findViewById<ImageView>(R.id.iv_close)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        return this
    }


    class MyAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setText(R.id.tv_name,item.name)
        }
    }

    var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(num:Int)
    }

    fun setDialogClickListener(onDialogClickListener: OnDialogClickListener) {
        listener = onDialogClickListener
    }

}