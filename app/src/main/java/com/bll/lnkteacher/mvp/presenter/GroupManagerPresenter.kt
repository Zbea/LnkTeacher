package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.Grade
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class GroupManagerPresenter(view: IContractView.IGroupManagerView) : BasePresenter<IContractView.IGroupManagerView>(view) {



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

    fun getGrades() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<List<Grade>>(view) {
            override fun failed(tBaseResult: BaseResult<List<Grade>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<Grade>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onGradeList(tBaseResult.data)
            }

        }, false)

    }


}