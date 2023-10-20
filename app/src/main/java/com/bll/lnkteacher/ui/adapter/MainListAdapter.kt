package com.bll.lnkteacher.ui.adapter

import android.widget.ImageView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.ItemList
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainListAdapter(layoutResId: Int, data: List<ItemList>?) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: ItemList) {
        val ivImage=helper.getView<ImageView>(R.id.iv_icon)
        helper.setText(R.id.tv_name,item.name)
        ivImage.setImageDrawable(if (item.isCheck) item.icon_check else item.icon)

    }

    fun updateItem(position: Int,checked: Boolean){
        mData[position].isCheck = checked
        notifyItemChanged(position)
    }


}
