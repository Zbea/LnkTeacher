package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.ClassGroupList
import com.bll.lnkteacher.mvp.model.Group
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class MainPresenter(view: IContractView.IMainView) : BasePresenter<IContractView.IMainView>(view) {


    fun getClassGroups() {

        val list = RetrofitManager.service.getListClassGroup()
        doRequest(list, object : Callback<ClassGroupList>(view) {
            override fun failed(tBaseResult: BaseResult<ClassGroupList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroupList>) {
                view.onClassList(tBaseResult?.data?.list)
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
                view.onGroupList(tBaseResult.data)
            }
        }, false)

    }


}