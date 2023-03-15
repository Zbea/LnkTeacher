package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.TestPaper
import com.bll.lnkteacher.mvp.model.TestPaperCorrectClass
import com.bll.lnkteacher.mvp.model.TestPaperGrade
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改详细功能
 */
class TestPaperCorrectDetailsPresenter(view: IContractView.ITestPaperCorrectDetailsView):BasePresenter<IContractView.ITestPaperCorrectDetailsView>(view) {

    fun getPaperImages(taskId:Int){
        val map=HashMap<String,Any>()
        map["taskId"]=taskId
        val list = RetrofitManager.service.getPaperCorrectImages(map)
        doRequest(list, object : Callback<List<TestPaper.ListBean>>(view) {
            override fun failed(tBaseResult: BaseResult<List<TestPaper.ListBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<TestPaper.ListBean>>) {
                view.onImageList(tBaseResult.data)
            }
        }, true)
    }

    fun getClassPapers(taskId:Int){
        val map=HashMap<String,Any>()
        map["examChangeId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getPaperCorrectClassList(map)
        doRequest(list, object : Callback<TestPaperCorrectClass>(view) {
            override fun failed(tBaseResult: BaseResult<TestPaperCorrectClass>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaperCorrectClass>) {
                view.onClassPapers(tBaseResult.data)
            }
        }, true)
    }

    fun getPaperGrade(taskId:Int){
        val map=HashMap<String,Any>()
        map["id"]=taskId
        val list = RetrofitManager.service.getPaperGrade(map)
        doRequest(list, object : Callback<List<TestPaperGrade>>(view) {
            override fun failed(tBaseResult: BaseResult<List<TestPaperGrade>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<TestPaperGrade>>) {
                view.onGrade(tBaseResult.data)
            }
        }, true)
    }

    fun commitPaperStudent(map:HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val commit = RetrofitManager.service.commitPaperStudent(body)
        doRequest(commit, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCorrectSuccess()
            }
        }, true)
    }

}