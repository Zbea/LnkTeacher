package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class GroupPresenter(view: IContractView.IGroupView) : BasePresenter<IContractView.IGroupView>(view) {

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

    fun quitGroup(id: Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", id),
        )
        val addGroup = RetrofitManager.service.quitGroup(body)
        doRequest(addGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onQuitSuccess()
            }
        }, true)

    }

    fun getGroupUsers(id: Int) {
        val map=HashMap<String,Any>()
        map["id"] = id
        val users = RetrofitManager.service.checkGroupUser(map)
        doRequest(users, object : Callback<List<GroupUser>>(view) {
            override fun failed(tBaseResult: BaseResult<List<GroupUser>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<GroupUser>>) {
                if (tBaseResult.data!=null)
                    view.getGroupUser(tBaseResult.data)
            }
        }, true)

    }

}