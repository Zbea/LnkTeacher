package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


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

    fun getExamImage(map:HashMap<String,Any>) {
        val list = RetrofitManager.service.getExamImage(map)
        doRequest(list, object : Callback<ExamList.ExamBean>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamList.ExamBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamList.ExamBean>) {
                if (tBaseResult.data!=null)
                    view.onExamImage(tBaseResult.data)
            }
        }, true)
    }

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

}