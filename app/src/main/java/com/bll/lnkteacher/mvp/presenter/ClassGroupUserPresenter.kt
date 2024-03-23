package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupUserPresenter(view: IContractView.IClassGroupUserView,val screen:Int) :
    BasePresenter<IContractView.IClassGroupUserView>(view) {


    fun getClassList(id: Int) {

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

//    /**
//     * 创建子群
//     */
//    fun createGroup(classId: Int, name: String, ids: List<Int>) {
//
//        val body = RequestUtils.getBody(
//            Pair.create("id", classId),
//            Pair.create("name", name),
//            Pair.create("studentIds", ids.toIntArray())
//        )
//        val list = RetrofitManager.service.createClassGroupUserGroup(body)
//        doRequest(list, object : Callback<Any>(view) {
//            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
//                return false
//            }
//
//            override fun success(tBaseResult: BaseResult<Any>) {
//                view.onCreateSuccess()
//            }
//        }, true)
//    }
//
//    /**
//     * 子群添加学生
//     */
//    fun addGroup(classId: Int, groupId: Int, ids: List<Int>) {
//        val body = RequestUtils.getBody(
//            Pair.create("classId", classId),
//            Pair.create("classGroupId", groupId),
//            Pair.create("studentIds", ids.toIntArray())
//        )
//        val list = RetrofitManager.service.addClassGroupUserGroup(body)
//        doRequest(list, object : Callback<Any>(view) {
//            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
//                return false
//            }
//
//            override fun success(tBaseResult: BaseResult<Any>) {
//                view.onAddSuccess()
//            }
//        }, true)
//    }

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
                view.onOutSuccess()
            }
        }, true)
    }

    fun editUser(id: Int, classId: Int, job: String) {

        val body = RequestUtils.getBody(
            Pair.create("id", id),
            Pair.create("classId", classId),
            Pair.create("job", job)
        )
        val change = RetrofitManager.service.changeJob(body)
        doRequest(change, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onEditSuccess()
            }
        }, true)
    }

}