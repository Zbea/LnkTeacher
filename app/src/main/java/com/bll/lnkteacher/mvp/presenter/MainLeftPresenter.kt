package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.AppUpdateBean
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager

class MainLeftPresenter(view: IContractView.IMainLeftView, val screen:Int=0):BasePresenter<IContractView.IMainLeftView>(view) {

    //获取更新信息
    fun getAppUpdate() {
        val list= RetrofitManager.service.onAppUpdate()
        doRequest(list, object : Callback<AppUpdateBean>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<AppUpdateBean>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AppUpdateBean>) {
                if (tBaseResult.data!=null)
                    view.onAppUpdate(tBaseResult.data)
            }
        }, false)
    }


    fun getClassGroups() {
        val list = RetrofitManager.service.getListClassGroup()
        doRequest(list, object : Callback<ClassGroupList>(view,screen,false) {
            override fun failed(tBaseResult: BaseResult<ClassGroupList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<ClassGroupList>) {
                if (tBaseResult.data!=null)
                    view.onClassList(tBaseResult.data?.list)
            }
        }, false)
    }

}