package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class GroupManagerPresenter(view: IContractView.IGroupManagerView) : BasePresenter<IContractView.IGroupManagerView>(view) {


    fun createClassGroup(name: String) {

        val body = RequestUtils.getBody(
            Pair.create("name", name)
        )
        val createGroup = RetrofitManager.service.createClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCreateClassGroupSuccess()
            }
        }, true)

    }


    fun createGroup(name: String, type: Int, classId: IntArray) {

        val body = RequestUtils.getBody(
            Pair.create("schoolName", name),
            Pair.create("type", type),
            Pair.create("selClassList", classId)
        )
        val createGroup = RetrofitManager.service.createGroup(body)
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCreateGroupSuccess()
            }
        }, true)

    }

    fun addGroup(name: String, type: Int, classId: IntArray) {

        val body = RequestUtils.getBody(
            Pair.create("schoolName", name),
            Pair.create("type", type),
            Pair.create("selClassList", classId)
        )
        val addGroup = RetrofitManager.service.addGroup(body)
        doRequest(addGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAddSuccess()
            }
        }, true)

    }



}