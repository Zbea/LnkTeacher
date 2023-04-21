package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupUserPresenter(view: IContractView.IClassGroupUserView) :
    BasePresenter<IContractView.IClassGroupUserView>(view) {


    fun getClassList(id: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", id)
        )
        val list = RetrofitManager.service.getClassGroupUserList(body)
        doRequest(list, object : Callback<List<ClassGroupUser>>(view) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroupUser>>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<List<ClassGroupUser>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onUserList(tBaseResult.data)

            }
        }, true)
    }

    fun outClassUser(id: Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", id)
        )
        val out = RetrofitManager.service.outClassGroupUser(body)
        doRequest(out, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onOutSuccess()
            }
        }, true)
    }

    fun editUser(id: Int, job: String) {

        val body = RequestUtils.getBody(
            Pair.create("id", id),
            Pair.create("job", job)
        )
        val change = RetrofitManager.service.changeJob(body)
        doRequest(change, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSuccess()
            }
        }, true)
    }

    fun editStatus(classId: Int,studentId:Int, status: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", classId),
            Pair.create("studentId", studentId),
            Pair.create("status", status)
        )
        val change = RetrofitManager.service.changeStatus(body)
        doRequest(change, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onStatusSuccess()
            }
        }, true)
    }

}