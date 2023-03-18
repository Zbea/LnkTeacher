package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.homework.HomeworkType
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent
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
        val type = RetrofitManager.service.getHomeworkType(map)
        doRequest(type, object : Callback<HomeworkType>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkType>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkType>) {
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
        val type = RetrofitManager.service.addHomeworkType(body)
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
        val list = RetrofitManager.service.getHomeworkContentList(map)
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
                view.onImageList(tBaseResult.data?.list)
            }
        }, false)
    }

}