package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.BookStore
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList
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
    fun addType(map: HashMap<String,Any>,boolean: Boolean) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.addPaperType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAddSuccess()
            }
        }, boolean)
    }

    /**
     * 删除题卷本
     */
    fun deleteType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.deletePaperType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
    }

    /**
     * 删除作业分类
     */
    fun deleteHomeworkType(map: HashMap<String,Any>) {
        val body=RequestUtils.getBody(map)
        val type = RetrofitManager.service.deleteHomeworkType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDeleteSuccess()
            }
        }, true)
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
        doRequest(type, object : Callback<HomeworkAssignDetailsList>(view) {
            override fun failed(tBaseResult: BaseResult<HomeworkAssignDetailsList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HomeworkAssignDetailsList>) {
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

    /**
     * 教辅本
     */
    fun getHomeworkBooks(map: HashMap<String,Any>) {

        val books = RetrofitManager.service.getHomeworkBooks(map)

        doRequest(books, object : Callback<BookStore>(view) {
            override fun failed(tBaseResult: BaseResult<BookStore>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<BookStore>) {
                view.onBook(tBaseResult.data)
            }

        }, false)

    }

}