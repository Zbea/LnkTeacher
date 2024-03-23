package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupChildPresenter(view: IContractView.IClassGroupChildView,val screen:Int) : BasePresenter<IContractView.IClassGroupChildView>(view) {


    fun getClassUser(id: Int) {
        val body = RequestUtils.getBody(
            Pair.create("classId", id)
        )
        val list = RetrofitManager.service.getClassGroupUserList(body)
        doRequest(list, object : Callback<List<ClassGroupUser>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroupUser>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroupUser>>) {
                if (tBaseResult.data?.isNotEmpty() == true)
                    view.onUserList(tBaseResult.data)
            }
        }, true)
    }

    fun getClassGroupChild(id: Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val list = RetrofitManager.service.getClassGroupChildList(map)
        doRequest(list, object : Callback<List<ClassGroup>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<ClassGroup>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<ClassGroup>>) {
                if (tBaseResult.data!=null)
                    view.onClassGroupChildList(tBaseResult.data)
            }
        }, true)
    }

    /**
     * 创建子群
     */
    fun createGroup(classId: Int, name: String, ids: List<Int>) {

        val body = RequestUtils.getBody(
            Pair.create("id", classId),
            Pair.create("name", name),
            Pair.create("studentIds", ids.toIntArray())
        )
        val list = RetrofitManager.service.createClassGroupUserGroup(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }

    /**
     * 子群添加学生
     */
    fun addGroup(classId: Int, groupId: Int, ids: List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("classId", classId),
            Pair.create("classGroupId", groupId),
            Pair.create("studentIds", ids.toIntArray())
        )
        val list = RetrofitManager.service.addClassGroupUserGroup(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }

    fun moveClassUser(map: HashMap<String,Any>) {
        val body = RequestUtils.getBody(map)
        val list = RetrofitManager.service.moveClassGroupUser(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }

    fun outClassUser(classId: Int,groupId: Int,userId: Int) {

        val body = RequestUtils.getBody(
            Pair.create("classId", classId),
            Pair.create("classGroupId", groupId),
            Pair.create("userId", userId)
        )
        val out = RetrofitManager.service.outClassGroupUser(body)
        doRequest(out, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }

    fun editClassGroup(name: String,classId: Int) {
        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("classId", classId)
        )
        val createGroup = RetrofitManager.service.editClassGroupChild(body)
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
        doRequest(createGroup, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }



}