package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.Grade
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperType
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager

/**
 * 考卷布置
 */
class TeachingPresenter(view: IContractView.ITeachingView) : BasePresenter<IContractView.ITeachingView>(view) {


    /**
     * 获取作业分类
     */
    fun getHomeworkType() {
        val map=HashMap<String,Any>()
        map["type"]=2
        map["size"]=100
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<TestPaperType>(view) {
            override fun failed(tBaseResult: BaseResult<TestPaperType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaperType>) {
                view.onHomeworkType(tBaseResult.data?.list)
            }
        }, false)
    }

    fun getGrades() {

        val editName = RetrofitManager.service.getCommonGrade()

        doRequest(editName, object : Callback<List<Grade>>(view) {
            override fun failed(tBaseResult: BaseResult<List<Grade>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<Grade>>) {
                if (tBaseResult.data != null)
                    view.onGrade(tBaseResult.data)
            }

        }, false)
    }

}