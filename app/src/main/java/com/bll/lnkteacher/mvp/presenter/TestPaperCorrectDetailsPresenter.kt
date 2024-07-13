package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改详细功能
 */
class TestPaperCorrectDetailsPresenter(view: IContractView.ITestPaperCorrectDetailsView,val screen:Int):BasePresenter<IContractView.ITestPaperCorrectDetailsView>(view) {

    fun getClassPapers(taskId:Int){
        val map=HashMap<String,Any>()
        map["examChangeId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getPaperCorrectClassList(map)
        doRequest(list, object : Callback<TestPaperClassUserList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TestPaperClassUserList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaperClassUserList>) {
                view.onClassPapers(tBaseResult.data)
            }
        }, true)
    }

    fun commitPaperStudent(map:HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaperStudent(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCorrectSuccess()
            }
        }, true)
    }

    fun sendClass(id:Int) {
        val map=HashMap<String,Any>()
        map["examChangeId"]=id
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.sendPaperCorrectClass(body)
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }

    fun setModule(map:HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val commit = RetrofitManager.service.setPaperCorrectModule(body)
        doRequest(commit, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, true)
    }

}