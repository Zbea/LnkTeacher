package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager

/**
 * 考卷批改详细功能
 */
class TestPaperCorrectDetailsPresenter(view: IContractView.ITestPaperCorrectDetailsView,val screen:Int):BasePresenter<IContractView.ITestPaperCorrectDetailsView>(view) {

    fun getPaperClassPapers(id:Int,classId:Int){
        val map=HashMap<String,Any>()
        map["id"]=id
        map["classId"]=classId
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

    fun complete(id:Int,classId:Int) {
        val map = HashMap<String, Any>()
        map["taskGroupId"] = id
        map["classId"] = classId
        val body = RequestUtils.getBody(map)
        val type = RetrofitManager.service.completeCorrectPaper(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCompleteSuccess()
            }
        }, true)
    }

}