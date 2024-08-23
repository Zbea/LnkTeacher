package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupUserPresenter(view: IContractView.IClassGroupUserView,val screen:Int) :
    BasePresenter<IContractView.IClassGroupUserView>(view) {


    fun getClassList(id: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", id)
        )
        val list = RetrofitManager.service.getClassGroupChildUser(body)
        doRequest(list, object : Callback<List<ClassGroupUser>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroupUser>>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<List<ClassGroupUser>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onUserList(tBaseResult.data)

            }
        }, true)
    }


    fun allowJoinGroup(classId: Int, join: Int) {
        val body = RequestUtils.getBody(
            Pair.create("classId", classId),
            Pair.create("isAllowJoin", join),
        )
        val list = RetrofitManager.service.allowJoinGroup(body)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAllowSuccess()
            }
        }, true)
    }

    fun outClassUser(classId: Int,groupId: Int,userId: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", classId),
            Pair.create("classGroupId", groupId),
            Pair.create("userIds", mutableListOf(userId))
        )
        val out = RetrofitManager.service.outClassGroupUser(body)
        doRequest(out, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onOutSuccess()
            }
        }, true)
    }

    fun editUser(id: Int, classId: Int, job: String) {

        val body = RequestUtils.getBody(
            Pair.create("id", id),
            Pair.create("classId", classId),
            Pair.create("job", job)
        )
        val change = RetrofitManager.service.changeJob(body)
        doRequest(change, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSuccess()
            }
        }, true)
    }

}