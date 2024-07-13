package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class TestPaperAssignPresenter(view: IContractView.ITestPaperAssignView) : BasePresenter<IContractView.ITestPaperAssignView>(view) {

    /**
     * 获取分类
     */
    fun getType(map: HashMap<String, Any>) {

        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<TypeList>(view) {
            override fun failed(tBaseResult: BaseResult<TypeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TypeList>) {
                if (tBaseResult.data!=null){
                    view.onType(tBaseResult.data)
                }
            }
        }, false)
    }

    fun addType(name:String,grade:Int){
        val body= RequestUtils.getBody(
            Pair.create("name",name),
            Pair.create("type",1),
            Pair.create("grade",grade)
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

    fun getPaperImages(taskId:Int){
        val map=HashMap<String,Any>()
        map["taskId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getPaperImages(map)
        doRequest(list, object : Callback<AssignPaperContentList>(view) {
            override fun failed(tBaseResult: BaseResult<AssignPaperContentList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AssignPaperContentList>) {
                if (tBaseResult.data!=null){
                    view.onImageList(tBaseResult.data?.list)
                }
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


    /**
     * 发送考卷
     */
    fun sendPapers(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val list = RetrofitManager.service.sendPapers(body)
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