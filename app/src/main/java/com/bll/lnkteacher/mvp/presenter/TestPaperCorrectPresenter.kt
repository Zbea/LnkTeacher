package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷批改
 */
class TestPaperCorrectPresenter(view: IContractView.ITestPaperCorrectView):BasePresenter<IContractView.ITestPaperCorrectView>(view) {

    fun getHomeworkCorrectList(map:HashMap<String,Any>) {
        val type = RetrofitManager.service.getHomeworkCorrectList(map)
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

    fun getPaperCorrectList(map:HashMap<String,Any>) {
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


    fun sendClass(id:Int, ids: List<Int>) {
        val map = HashMap<String, Any>()
        map["taskGroupId"] = id
        map["classIds"]=ids
        val body = RequestUtils.getBody(map)
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

}
