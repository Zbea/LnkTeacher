package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class HomeworkPaperAssignPresenter(view: IContractView.IHomeworkPaperAssignView) : BasePresenter<IContractView.IHomeworkPaperAssignView>(view) {

    fun getList(map: HashMap<String,Any>){
        val list = RetrofitManager.service.getHomeworkList(map)
        doRequest(list, object : Callback<AssignContent>(view) {
            override fun failed(tBaseResult: BaseResult<AssignContent>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AssignContent>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun getImages(taskId:Int){
        val map=HashMap<String,Any>()
        map["taskId"]=taskId
        map["size"]=100
        val list = RetrofitManager.service.getHomeworkImages(map)
        doRequest(list, object : Callback<AssignContent>(view) {
            override fun failed(tBaseResult: BaseResult<AssignContent>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AssignContent>) {
                if (!tBaseResult.data?.list.isNullOrEmpty()){
                    view.onImageList(tBaseResult.data?.list)
                }
            }
        }, false)
    }

    fun commitHomeworkReel(map:HashMap<String,Any>){
        val boay=RequestUtils.getBody(map)
        val list = RetrofitManager.service.commitHomeworkReel(boay)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
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