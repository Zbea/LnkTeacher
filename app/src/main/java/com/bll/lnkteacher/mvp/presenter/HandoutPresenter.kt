package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.HandoutList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager

/**
 * 应用相关
 */
class HandoutPresenter(view: IContractView.IHandoutView, val screen:Int) : BasePresenter<IContractView.IHandoutView>(view) {


    fun getTypeList() {
        val app = RetrofitManager.service.getHandoutTypes()
        doRequest(app, object : Callback<List<String>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<String>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<String>>) {
                if (tBaseResult.data!=null)
                    view.onType(tBaseResult.data)
            }
        }, false)
    }


    fun getList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getHandoutList(map)

        doRequest(app, object : Callback<HandoutList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<HandoutList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<HandoutList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, true)
    }


    fun delete(map: HashMap<String, Any> ) {

        val requestBody= RequestUtils.getBody(map)
        val download = RetrofitManager.service.onDeleteHandout(requestBody)

        doRequest(download, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }

        }, true)

    }

}