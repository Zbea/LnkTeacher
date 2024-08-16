package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ExamListPresenter(view: IContractView.IExamListView, val screen:Int) : BasePresenter<IContractView.IExamListView>(view) {

    fun getExamList(map:HashMap<String,Any>) {
        val list = RetrofitManager.service.getExamList(map)
        doRequest(list, object : Callback<ExamList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun deleteExamList(map:HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val list = RetrofitManager.service.deleteExamList(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                if (tBaseResult.data!=null)
                    view.onDeleteSuccess()
            }
        }, true)
    }

    fun sendClass(id:Int, ids: List<Int>) {
        val map = HashMap<String, Any>()
        map["id"] = id
        map["classIds"]=ids
        val body = RequestUtils.getBody(map)
        val type = RetrofitManager.service.sendExamCorrectClass(body)
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