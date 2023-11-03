package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupPresenter(view: IContractView.IClassGroupView) : BasePresenter<IContractView.IClassGroupView>(view) {

    fun createClassGroup(name: String,grade:Int,job:Int) {

        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("grade", grade),
            Pair.create("job", job)
        )
        val createGroup = RetrofitManager.service.createClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCreateSuccess()
            }
        }, true)

    }

    fun editClassGroup(classId: Int,name: String,job: Int,grade: Int) {

        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("job", job),
            Pair.create("grade", grade),
            Pair.create("classId", classId)
        )
        val createGroup = RetrofitManager.service.editClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSuccess()
            }
        }, true)

    }

    fun dissolveClassGroup(classId: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", classId)
        )
        val createGroup = RetrofitManager.service.dissolveClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDissolveSuccess()
            }
        }, true)
    }

}