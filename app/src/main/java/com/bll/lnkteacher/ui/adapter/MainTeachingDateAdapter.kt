package com.bll.lnkteacher.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.defaultFromStyle
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Date
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainTeachingDateAdapter(layoutResId: Int, data: List<Date>?) :
    BaseQuickAdapter<Date, BaseViewHolder>(layoutResId, data) {

    @SuppressLint("WrongConstant")
    override fun convert(helper: BaseViewHolder, item: Date) {
        val tvDay = helper.getView<TextView>(R.id.tv_day)
        val tvLunar=helper.getView<TextView>(R.id.tv_lunar)
        tvDay.text = if (item.day == 0) "" else item.day.toString()
        if (item.isNow)
            tvDay.typeface = defaultFromStyle(BOLD)

        val str = if (!item.solar.solar24Term.isNullOrEmpty()) {
            item.solar.solar24Term
        } else {
            if (!item.solar.solarFestivalName.isNullOrEmpty()) {
                item.solar.solarFestivalName
            } else {
                if (!item.lunar.lunarFestivalName.isNullOrEmpty()) {
                    item.lunar.lunarFestivalName
                } else {
                    item.lunar.getChinaDayString(item.lunar.lunarDay)
                }
            }
        }
        tvLunar.text=str

        if (item.dateEvent!=null){
            helper.setText(R.id.tv_content,item.dateEvent.content)
        }
        else{
            helper.setText(R.id.tv_content,"")
        }

    }


}
