package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 考卷布置
 */
class HomeworkAssignPresenter(view: IContractView.IHomeworkAssignView) : BasePresenter<IContractView.IHomeworkAssignView>(view) {

    /**
     * 获取作业本列表
     */
    fun getTypeList(map: HashMap<String, Any>) {
        val type = RetrofitManager.service.getPaperType(map)
        doRequest(type, object : Callback<TypeList>(view) {
            override fun failed(tBaseResult: BaseResult<TypeList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TypeList>) {
                if(tBaseResult.data!=null){
                    view.onTypeList(tBaseResult.data)
                }
            }
        }, false)
    }

    /**
     * 添加作业本
     */
    fun addType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.addPaperType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAddSuccess()
            }
        }, true)
    }

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
                if (tBaseResult.data!=null){
                    view.onImageList(tBaseResult.data?.list)
                }
            }
        }, false)
    }

    fun commitHomework(map:HashMap<String,Any>){
        val boay=RequestUtils.getBody(map)
        val list = RetrofitManager.service.commitHomework(boay)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onCommitSuccess()
            }
        }, true)
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

    fun onDetails(grade:Int) {
        val map=HashMap<String,Any>()
        map["page"]=1
        map["size"]=10
        map["grade"]=grade
        val type = RetrofitManager.service.assignHomeworkDetails(map)
        doRequest(type, object : Callback<HomeworkAssignDetails>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkAssignDetails>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkAssignDetails>) {
                if (tBaseResult.data!=null){
                    view.onDetails(tBaseResult.data)
                }
            }
        }, true)
    }

    fun deleteDetails(id:Int) {
        val ids= arrayOf(id)
        val map=HashMap<String,Any>()
        map["ids"]=ids
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.deleteAssignDetails(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDetailsDeleteSuccess()
            }
        }, true)
    }

}