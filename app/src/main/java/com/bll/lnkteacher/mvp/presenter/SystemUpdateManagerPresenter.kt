package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.SystemUpdateInfo
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager
import com.bll.lnkteacher.net.system.BasePresenter1
import com.bll.lnkteacher.net.system.BaseResult1
import com.bll.lnkteacher.net.system.Callback1


class SystemUpdateManagerPresenter(view: IContractView.ISystemView, val screen: Int) : BasePresenter1<IContractView.ISystemView>(view) {

    fun checkSystemUpdate(map: Map<String,String>) {

        val body = RequestUtils.getBody(map)

        val request = RetrofitManager.service1.RELEASE_CHECK_UPDATE(body)
        doRequest(request, object : Callback1<SystemUpdateInfo>(view, screen,false) {
            override fun failed(tBaseResult: BaseResult1<SystemUpdateInfo>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult1<SystemUpdateInfo>) {
                if (tBaseResult.Data!=null)
                    view.onUpdateInfo(tBaseResult.Data)
            }
        }, false)
    }
}