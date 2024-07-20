package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class TestPaperAssignContentPresenter(view: IContractView.ITestPaperAssignContentView) : BasePresenter<IContractView.ITestPaperAssignContentView>(view) {

    fun getTestPaperContentList(map: HashMap<String,Any>){
        val list = RetrofitManager.service.getTestPaperContentList(map)
        doRequest(list, object : Callback<AssignPaperContentList>(view) {
            override fun failed(tBaseResult: BaseResult<AssignPaperContentList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AssignPaperContentList>) {
                if (tBaseResult.data!=null){
                    view.onList(tBaseResult.data)
                }
            }
        }, true)
    }

    fun deletePapers(ids:List<Int>){
        val map=HashMap<String,Any>()
        map["ids"]=ids.toIntArray()
        val body=RequestUtils.getBody(map)

        val list = RetrofitManager.service.deleteTestPaperContent(body)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

    /**
     * 布置考卷
     */
    fun commitPapers(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val list = RetrofitManager.service.sendTestPaperContent(body)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSendSuccess()
            }
        }, true)
    }

}