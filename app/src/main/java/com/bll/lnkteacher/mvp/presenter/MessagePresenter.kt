package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

class MessagePresenter(view: IContractView.IMessageView):BasePresenter<IContractView.IMessageView>(view) {

    fun sendMessage(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val send=RetrofitManager.service.sendMessage(body)
        doRequest(send, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSend()
            }
        },true)
    }

    fun deleteMessages(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val delete=RetrofitManager.service.deleteMessages(body)
        doRequest(delete, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        },true)
    }

    fun getList(map: HashMap<String,Any>,boolean: Boolean){
        val list=RetrofitManager.service.getMessages(map)
        doRequest(list, object : Callback<Message>(view) {
            override fun failed(tBaseResult: BaseResult<Message>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Message>) {
                if (tBaseResult?.data!=null)
                    view.onList(tBaseResult.data)
            }
        },boolean)
    }

}