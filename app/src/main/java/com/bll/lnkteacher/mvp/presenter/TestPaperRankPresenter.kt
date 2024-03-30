package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.exam.ExamRankList
import com.bll.lnkteacher.mvp.model.testpaper.RankBean
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class TestPaperRankPresenter(view: IContractView.ITestPaperRankView, val screen:Int):BasePresenter<IContractView.ITestPaperRankView>(view) {

    fun getPaperGrade(taskId:Int){
        val map=HashMap<String,Any>()
        map["id"]=taskId
        val list = RetrofitManager.service.getPaperGrade(map)
        doRequest(list, object : Callback<List<RankBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<RankBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<RankBean>>) {
                view.onGrade(tBaseResult.data)
            }
        }, true)
    }

    fun getExamGrade(id:Int){
        val map=HashMap<String,Any>()
        map["schoolExamJobId"]=id
        val list = RetrofitManager.service.getExamScores(map)
        doRequest(list, object : Callback<ExamRankList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ExamRankList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ExamRankList>) {
                view.onExamGrade(tBaseResult.data)
            }
        }, true)
    }
}