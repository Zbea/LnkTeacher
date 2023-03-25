package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.Grade
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.model.group.Group
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

    fun getGrades() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<List<Grade>>(view) {
            override fun failed(tBaseResult: BaseResult<List<Grade>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<Grade>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onList(tBaseResult.data)
            }

        }, false)

    }


}