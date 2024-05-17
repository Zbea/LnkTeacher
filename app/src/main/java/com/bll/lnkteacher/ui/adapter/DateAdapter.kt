package com.bll.lnkteacher.ui.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.defaultFromStyle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.Date
import com.bll.lnkteacher.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File

class DateAdapter(layoutResId: Int, data: List<Date>?) :
    BaseQuickAdapter<Date, BaseViewHolder>(layoutResId, data) {

    @SuppressLint("WrongConstant")
    override fun convert(helper: BaseViewHolder, item: Date) {
        val tvDay = helper.getView<TextView>(R.id.tv_day)
        val tvLunar=helper.getView<TextView>(R.id.tv_lunar)
        val ivImage=helper.getView<ImageView>(R.id.iv_image)
        tvDay.text = if (item.day == 0) "" else item.day.toString()
        if (item.isNow)
            tvDay.typeface = defaultFromStyle(BOLD)
        if (item.isNowMonth) {
            tvDay.setTextColor(mContext.getColor(R.color.black))
            tvLunar.setTextColor(mContext.getColor(R.color.gray))
        } else {
            tvDay.setTextColor(mContext.getColor(R.color.black_90))
            tvLunar.setTextColor(mContext.getColor(R.color.black_90))
        }

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

        if (item.year!=0){
            val path= FileAddress().getPathImage("date", DateUtils.longToStringCalender(item.time))+"/draw.png"
            if (File(path).exists()){
                try {
                    ivImage.setImageBitmap(BitmapFactory.decodeFile(path))
                } catch (e: Exception) {
                }
            }
            else{
                ivImage.visibility= View.GONE
            }
        }
        else{
            ivImage.visibility= View.GONE
        }

    }


}
