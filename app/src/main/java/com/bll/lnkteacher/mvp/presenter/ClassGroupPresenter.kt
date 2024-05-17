package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupPresenter(view: IContractView.IClassGroupView,val screen:Int) : BasePresenter<IContractView.IClassGroupView>(view) {

    fun getClassGroups() {
        val list = RetrofitManager.service.getListClassGroup()
        doRequest(list, object : Callback<ClassGroupList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ClassGroupList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroupList>) {
                if (tBaseResult.data!=null)
                    view.onClasss(tBaseResult.data?.list)
            }
        }, false)

    }

    fun createClassGroup(name: String,grade:Int) {

        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("grade", grade),
        )
        val createGroup = RetrofitManager.service.createClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)

    }

    fun editClassGroup(name: String,grade:Int,classId: Int) {
        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("grade", grade),
            Pair.create("classGroupId", classId)
        )
        val createGroup = RetrofitManager.service.editClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)

    }

    fun addClassGroup(id: Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", id),
        )
        val createGroup = RetrofitManager.service.addClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)

    }

    /**
     * 创建者解散班群
     */
    fun dissolveClassGroup(classId: Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", classId)
        )
        val createGroup = RetrofitManager.service.dissolveClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }


    /**
     * 添加课程表
     */
    fun uploadClassGroup(classId: Int,imageUrl:String) {
        val body = RequestUtils.getBody(
            Pair.create("id", classId),
            Pair.create("imageUrl", imageUrl)
        )
        val createGroup = RetrofitManager.service.uploadClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onUploadSuccess()
            }
        }, true)
    }

    /**
     * 老师退出班群
     */
    fun outClassGroup(classId: Int) {

        val body = RequestUtils.getBody(
            Pair.create("id", classId)
        )
        val createGroup = RetrofitManager.service.outClassGroup(body)
        doRequest(createGroup, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }

}