package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager


class ClassGroupPresenter(view: IContractView.IClassGroupView,val screen:Int=0) : BasePresenter<IContractView.IClassGroupView>(view) {

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

    //班群信息
    fun onClassGroupInfo(id:Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val quit= RetrofitManager.service.getGroupInfo(map)
        doRequest(quit, object : Callback<ClassGroup>(view,screen) {
            override fun failed(tBaseResult: BaseResult<ClassGroup>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroup>) {
                view.onClassInfo(tBaseResult.data)
            }
        }, false)
    }

    fun getClassGroupSubject(classGroupId: Int) {
        val map=HashMap<String,Any>()
        map["classGroupId"]=classGroupId
        val list = RetrofitManager.service.getClassGroupSubjects(map)
        doRequest(list, object : Callback<List<String>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<List<String>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<List<String>>) {
                if (tBaseResult.data!=null)
                    view.onSubjects(tBaseResult.data)
            }
        }, true)

    }

    fun createClassGroup(name: String,grade:Int,type:Int) {

        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("grade", grade),
            Pair.create("type", type)
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

    /**
     * 创建子群
     */
    fun createGroupChild(classId: Int, name: String) {

        val body = RequestUtils.getBody(
            Pair.create("id", classId),
            Pair.create("name", name)
//            Pair.create("studentIds", ids.toIntArray())
        )
        val list = RetrofitManager.service.createClassGroupChild(body)
        doRequest(list, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onSuccess()
            }
        }, true)
    }


    fun editClassGroup(name: String,grade:Int,classId: Int,classGroupId: Int) {
        val body = RequestUtils.getBody(
            Pair.create("name", name),
            Pair.create("grade", grade),
            Pair.create("id", classGroupId),
            Pair.create("classId", classId)
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

    fun editClassGroupChild(name: String,classId: Int) {
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
     * 上传老师排课表
     */
    fun uploadClassSchedule(imageUrl:String) {
        val body = RequestUtils.getBody(
            Pair.create("courseUrl", imageUrl)
        )
        val createGroup = RetrofitManager.service.uploadClassSchedule(body)
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