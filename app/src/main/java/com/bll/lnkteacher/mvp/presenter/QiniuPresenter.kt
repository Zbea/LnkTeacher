package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager

class QiniuPresenter(view: IContractView.IQiniuView,val screen:Int=0):
    BasePresenter<IContractView.IQiniuView>(view) {

    fun getToken(){
        val token = RetrofitManager.service.getQiniuToken()
        doRequest(token, object : Callback<String>(view) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                if (tBaseResult.data!=null)
                    view.onToken(tBaseResult.data)
            }
        }, true)
    }

    fun getToken(boolean: Boolean){
        val token = RetrofitManager.service.getQiniuToken()
        doRequest(token, object : Callback<String>(view,screen,false,false) {
            override fun failed(tBaseResult: BaseResult<String>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<String>) {
                if (tBaseResult.data!=null)
                    view.onToken(tBaseResult.data)
            }
        }, boolean)
    }



}