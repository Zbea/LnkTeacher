package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.TestPaper
import com.bll.lnkteacher.mvp.model.TestPaperType
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class TestPaperAssignPresenter(view: IContractView.ITestPaperAssignView) : BasePresenter<IContractView.ITestPaperAssignView>(view) {

    /**
     * 获取分类
     */
    fun getType() {
        val map=HashMap<String,Any>()
        map["type"]=1
        map["size"]=20
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<TestPaperType>(view) {
            override fun failed(tBaseResult: BaseResult<TestPaperType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaperType>) {
                view.onType(tBaseResult.data?.list)
            }
        }, false)
    }

    fun addType(name:String){
        val body= RequestUtils.getBody(
            Pair.create("name",name),
            Pair.create("type",1)
        )
        val add = RetrofitManager.service.addPaperType(body)
        doRequest(add, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onTypeSuccess()
            }
        }, true)
    }

    fun getPaperList(map: HashMap<String,Any>){
        val list = RetrofitManager.service.getPaperList(map)
        doRequest(list, object : Callback<TestPaper>(view) {
            override fun failed(tBaseResult: BaseResult<TestPaper>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaper>) {
                view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun getPaperImages(taskId:Int){
        val map=HashMap<String,Any>()
        map["taskId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getPaperImages(map)
        doRequest(list, object : Callback<TestPaper>(view) {
            override fun failed(tBaseResult: BaseResult<TestPaper>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TestPaper>) {
                view.onImageList(tBaseResult.data)
            }
        }, false)
    }

    fun deletePapers(ids:List<Int>){
        val map=HashMap<String,Any>()
        map["ids"]=ids.toIntArray()
        val body=RequestUtils.getBody(map)

        val list = RetrofitManager.service.deletePaper(body)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

}