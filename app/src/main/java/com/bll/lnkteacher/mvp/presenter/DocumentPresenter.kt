package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager

class DocumentPresenter(view: IContractView.IDocumentView, val screen:Int) : BasePresenter<IContractView.IDocumentView>(view){

    fun sendDocument(map :HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.sendDocument(body)
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }

}