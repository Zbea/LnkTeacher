package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.FriendList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class AccountInfoPresenter(view: IContractView.IAccountInfoView,val screen:Int) : BasePresenter<IContractView.IAccountInfoView>(view) {

    fun editName(name: String) {
        val body = RequestUtils.getBody(
            Pair.create("nickName", name)
        )
        val editName = RetrofitManager.service.editName(body)
        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditNameSuccess()
            }
        }, true)
    }


    fun editSchool(id: Int) {
        val map=HashMap<String,Any>()
        map["schoolId"]=id
        val body = RequestUtils.getBody(map)
        val editName = RetrofitManager.service.editSchool(body)

        doRequest(editName, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSchool()
            }
        }, true)
    }

    fun onBindFriend(account: String) {
        val map=HashMap<String,Any>()
        map["account"]=account
        val body = RequestUtils.getBody(map)
        val editName = RetrofitManager.service.onBindFriend(body)
        doRequest(editName, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onBind()
            }
        }, true)
    }

    fun unbindFriend(id: Int) {
        val map=HashMap<String,Any>()
        map["ids"]= arrayListOf(id).toArray()
        val body = RequestUtils.getBody(map)
        val editName = RetrofitManager.service.onUnbindFriend(body)
        doRequest(editName, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onUnbind()
            }
        }, true)
    }

    fun getFriends() {
        val editName = RetrofitManager.service.onFriendList()
        doRequest(editName, object : Callback<FriendList>(view) {
            override fun failed(tBaseResult: BaseResult<FriendList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<FriendList>) {
                if (tBaseResult.data!=null)
                    view.onListFriend(tBaseResult.data)
            }
        }, true)
    }


}