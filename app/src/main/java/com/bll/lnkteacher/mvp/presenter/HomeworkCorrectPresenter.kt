package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改
 */
class HomeworkCorrectPresenter(view: IContractView.IHomeworkCorrectView):BasePresenter<IContractView.IHomeworkCorrectView>(view) {

    fun getList(map:HashMap<String,Any>) {
        val type = RetrofitManager.service.getPaperCorrectList(map)
        doRequest(type, object : Callback<CorrectList>(view) {
            override fun failed(tBaseResult: BaseResult<CorrectList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CorrectList>) {
                if (tBaseResult.data!=null){
                    view.onList(tBaseResult.data)
                }
            }
        }, false)
    }

    fun deleteCorrect(id:Int) {
        val ids= arrayOf(id)
        val map=HashMap<String,Any>()
        map["ids"]=ids
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.deletePaperCorrectList(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

    fun sendClass(id:Int) {
        val map=HashMap<String,Any>()
        map["examChangeId"]=id
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.sendPaperCorrectClass(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }

    fun sendGroup(id:Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.sendPaperCorrectGroup(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }


}