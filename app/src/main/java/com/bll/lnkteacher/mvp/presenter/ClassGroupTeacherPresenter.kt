package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*


class ClassGroupTeacherPresenter(view: IContractView.IClassGroupTeacherView) :
    BasePresenter<IContractView.IClassGroupTeacherView>(view) {


    fun getClassList(id: Int) {
        val map=HashMap<String,Any>()
        map["id"]=id
        val list = RetrofitManager.service.getClassGroupTeacherList(map)
        doRequest(list, object : Callback<ClassGroupList>(view) {
            override fun failed(tBaseResult: BaseResult<ClassGroupList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<ClassGroupList>) {
                if (tBaseResult.data?.teacherList!=null)
                    view.onUserList(tBaseResult.data?.teacherList)
            }
        }, true)
    }

    fun outTeacher(map: HashMap<String,Any>) {
        val body = RequestUtils.getBody(map)
        val out = RetrofitManager.service.outTeacher(body)
        doRequest(out, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onOutSuccess()
            }
        }, true)
    }

    fun transfer(map: HashMap<String, Any>) {
        val body = RequestUtils.getBody(map)
        val out = RetrofitManager.service.transferTeacher(body)
        doRequest(out, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.onTransferSuccess()
            }
        }, true)
    }


}