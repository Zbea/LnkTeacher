package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class SchoolPresenter(view: IContractView.ISchoolView) : BasePresenter<IContractView.ISchoolView>(view) {

    fun getSchool() {
        val grade = RetrofitManager.service.getCommonSchool()
        doRequest(grade, object : Callback<MutableList<SchoolBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<SchoolBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<SchoolBean>>) {
                if (!tBaseResult.data.isNullOrEmpty())
                    view.onListSchools(tBaseResult.data)
            }
        }, true)
    }

}