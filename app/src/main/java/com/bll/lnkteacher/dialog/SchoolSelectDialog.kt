package com.bll.lnkteacher.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.mvp.model.AreaBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 学校选择
 */
class SchoolSelectDialog(val context: Context, private val beans:MutableList<SchoolBean>){

    private var dialog:Dialog?=null
    private var provincePops= mutableListOf<PopupBean>()
    private var cityPops= mutableListOf<PopupBean>()
    private var areaPops= mutableListOf<PopupBean>()
    private var schools= mutableListOf<PopupBean>()
    private var provinceStr=""
    private var cityStr=""
    private var areaStr=""
    private var provinces= mutableListOf<AreaBean>()
    private var citys= mutableListOf<AreaBean>()
    private var areas= mutableListOf<AreaBean>()
    private var school:PopupBean?=null

    fun builder(): SchoolSelectDialog {
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_school)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()

        val areaJson = FileUtils.readFileContent(context.resources.assets.open("city.json"))
        val type= object : TypeToken<List<AreaBean>>() {}.type
        provinces= Gson().fromJson(areaJson, type)

        for (i in provinces.indices){
            provincePops.add(PopupBean(i,provinces[i].value,i==0))
        }
        citys=provinces[0].children
        for (i in citys.indices){
            cityPops.add(PopupBean(i,citys[i].value,i==0))
        }
        areas=citys[0].children
        for (i in areas.indices){
            areaPops.add(PopupBean(i,areas[i].value,i==0))
        }
        provinceStr=provincePops[0].name
        cityStr=cityPops[0].name
        areaStr=areaPops[0].name
        getSchool()

        val tvCancel=dialog?.findViewById<TextView>(R.id.tv_cancel)
        val tvOk=dialog?.findViewById<TextView>(R.id.tv_ok)
        val tvProvince=dialog?.findViewById<TextView>(R.id.tv_province)
        tvProvince?.text=provinceStr
        val tvCity=dialog?.findViewById<TextView>(R.id.tv_city)
        tvCity?.text=cityStr
        val tvArea=dialog?.findViewById<TextView>(R.id.tv_area)
        tvArea?.text=areaStr
        val tvSchool=dialog?.findViewById<TextView>(R.id.tv_school)
        tvProvince?.setOnClickListener {
            PopupRadioList(context,provincePops,tvProvince,tvProvince.width,5).builder().setOnSelectListener{
                school=null
                tvSchool?.text=""
                provinceStr=it.name
                tvProvince.text=provinceStr
                cityPops.clear()
                areaPops.clear()

                citys=provinces[it.id].children
                for (i in citys.indices){
                    cityPops.add(PopupBean(i,citys[i].value,i==0))
                }
                cityStr=cityPops[0].name
                tvCity?.text=cityStr

                areas=citys[0].children
                for (i in areas.indices){
                    areaPops.add(PopupBean(i,areas[i].value,i==0))
                }
                areaStr=areaPops[0].name
                tvArea?.text=areaStr
                getSchool()
            }
        }
        tvCity?.setOnClickListener {
            PopupRadioList(context,cityPops,tvCity,tvCity.width,5).builder().setOnSelectListener{
                school=null
                tvSchool?.text=""
                cityStr=it.name
                tvCity.text=cityStr
                areaPops.clear()
                areas=citys[it.id].children
                for (i in areas.indices){
                    areaPops.add(PopupBean(i,areas[i].value,i==0))
                }
                areaStr=areaPops[0].name
                tvArea?.text=areaStr
                getSchool()
            }
        }
        tvArea?.setOnClickListener {
            PopupRadioList(context,areaPops,tvArea,tvArea.width,5).builder().setOnSelectListener{
                school=null
                tvSchool?.text=""
                areaStr=it.name
                tvArea.text=areaStr
                getSchool()
            }
        }

        tvSchool?.setOnClickListener {
            PopupRadioList(context,schools,tvSchool,tvSchool.width,5).builder().setOnSelectListener{
                school=it
                tvSchool.text=it.name
            }
        }

        tvCancel?.setOnClickListener {
            dialog?.dismiss()
        }

        tvOk?.setOnClickListener {
            if (school!=null){
                onClickListener?.onSelect(school!!)
                dialog?.dismiss()
            }
            else{
                SToast.showText("请选择学校")
            }
        }
        return this
    }


    private fun getSchool(){
        schools.clear()
        for (item in beans){
            if (item.province==provinceStr&&item.city==cityStr&&item.area==areaStr){
                schools.add(PopupBean(item.id,item.schoolName))
            }
        }
    }

    fun show(){
        dialog?.show()
    }


    private var onClickListener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onSelect(schoolBean: PopupBean)
    }

    fun setOnDialogClickListener(onClickListener: OnDialogClickListener?) {
        this.onClickListener = onClickListener
    }

}