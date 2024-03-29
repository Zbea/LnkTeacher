package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
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


    fun logout() {
        val logout = RetrofitManager.service.logout()
        doRequest(logout, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onLogout()
            }
        }, true)
    }


}