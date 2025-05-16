package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignSearchBean
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager

/**
 * 考卷布置
 */
class HomeworkPaperAssignPresenter(view: IContractView.IHomeworkPaperAssignView) : BasePresenter<IContractView.IHomeworkPaperAssignView>(view) {

    fun getList(map: HashMap<String,Any>){
        val list = RetrofitManager.service.getHomeworkList(map)
        doRequest(list, object : Callback<AssignPaperContentList>(view) {
            override fun failed(tBaseResult: BaseResult<AssignPaperContentList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AssignPaperContentList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun getListSearch(map: HashMap<String,Any>){
        val list = RetrofitManager.service.getHomeworkAssignContent(map)
        doRequest(list, object : Callback<MutableList<HomeworkAssignSearchBean>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<HomeworkAssignSearchBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<HomeworkAssignSearchBean>>) {
                if (tBaseResult.data!=null)
                    view.onSearchAssignList(tBaseResult.data)
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

    fun setHomeworkAutoAssign(map:HashMap<String,Any>){
        val boay=RequestUtils.getBody(map)
        val list = RetrofitManager.service.setHomeworkAutoAssign(boay)
        doRequest(list, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSetAutoAssign()
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

}