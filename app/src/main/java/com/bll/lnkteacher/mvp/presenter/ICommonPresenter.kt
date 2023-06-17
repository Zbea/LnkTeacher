package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class ICommonPresenter(view: IContractView.ICommonView) : BasePresenter<IContractView.ICommonView>(view) {


    fun getClassGroups() {

        val list = RetrofitManager.service.getListClassGroup()
        doRequest(list, object : Callback<ClassGroupList>(view) {
            override fun failed(tBaseResult: BaseResult<ClassGroupList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroupList>) {
                if (tBaseResult.data?.list?.isNotEmpty() == true)
                    view.onClassList(tBaseResult.data?.list)
            }
        }, false)

    }

    fun getGroups() {

        val list = RetrofitManager.service.getGroupList()
        doRequest(list, object : Callback<MutableList<Group>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<Group>>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<MutableList<Group>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onGroupList(tBaseResult.data)
            }
        }, false)

    }

    fun getCommon() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<CommonData>(view) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CommonData>) {
                if (tBaseResult.data!=null)
                    view.onCommon(tBaseResult.data)
            }

        }, false)

    }


}