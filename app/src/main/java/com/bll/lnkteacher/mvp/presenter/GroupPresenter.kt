package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class GroupPresenter(view: IContractView.IGroupView) : BasePresenter<IContractView.IGroupView>(view) {

    fun getGroups(boolean: Boolean) {

        val list = RetrofitManager.service.getGroupList()
        doRequest(list, object : Callback<MutableList<Group>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<Group>>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<MutableList<Group>>) {
                view.onGroupList(tBaseResult.data)
            }
        }, boolean)

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
        map.put("id",id)
        val users = RetrofitManager.service.checkGroupUser(map)
        doRequest(users, object : Callback<List<GroupUser>>(view) {
            override fun failed(tBaseResult: BaseResult<List<GroupUser>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<GroupUser>>) {
                view.getGroupUser(tBaseResult.data)
            }
        }, true)

    }

}