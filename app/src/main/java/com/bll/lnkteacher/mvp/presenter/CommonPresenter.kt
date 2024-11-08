package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.AppUpdateBean
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class CommonPresenter(view: IContractView.ICommonView) : BasePresenter<IContractView.ICommonView>(view) {


    fun getCommon() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<CommonData>(view,0,false) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonData>) {
                if (tBaseResult.data!=null)
                    view.onCommon(tBaseResult.data)
            }

        }, false)

    }

    fun getSchool() {
        val grade = RetrofitManager.service.getCommonSchool()
        doRequest(grade, object : Callback<MutableList<SchoolBean>>(view,0) {
            override fun failed(tBaseResult: BaseResult<MutableList<SchoolBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<SchoolBean>>) {
                if (!tBaseResult.data.isNullOrEmpty())
                    view.onListSchools(tBaseResult.data)
            }
        }, false)
    }


}