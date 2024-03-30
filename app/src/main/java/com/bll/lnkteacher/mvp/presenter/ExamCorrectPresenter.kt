package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ExamCorrectPresenter(view: IContractView.IExamCorrectView, val screen:Int) : BasePresenter<IContractView.IExamCorrectView>(view) {

    fun getExamClassUser(map:HashMap<String,Any>) {
        val list = RetrofitManager.service.getExamClass(map)
        doRequest(list, object : Callback<ExamClassUserList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamClassUserList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamClassUserList>) {
                if (tBaseResult.data!=null)
                    view.onExamClassUser(tBaseResult.data)
            }
        }, true)
    }

    fun onExamCorrectComplete(map:HashMap<String,Any>) {
        val body = RequestUtils.getBody(map)
        val list = RetrofitManager.service.onExamCorrectUserComplete(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCorrectSuccess()
            }
        }, true)
    }

}