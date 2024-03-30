package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ExamCorrectListPresenter(view: IContractView.IExamCorrectListView, val screen:Int) : BasePresenter<IContractView.IExamCorrectListView>(view) {

    fun getExamCorrectList() {
        val list = RetrofitManager.service.getExamCorrectList()
        doRequest(list, object : Callback<ExamCorrectList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamCorrectList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamCorrectList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun onExamCorrectComplete(id:Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val body = RequestUtils.getBody(map)
        val list = RetrofitManager.service.onExamCorrectComplete(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }
}