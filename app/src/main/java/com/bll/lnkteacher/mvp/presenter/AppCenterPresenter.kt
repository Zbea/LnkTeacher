package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.AppList
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 应用相关
 */
class AppCenterPresenter(view: IContractView.IAPPView,val screen:Int) : BasePresenter<IContractView.IAPPView>(view) {


    fun getTypeList() {

        val app = RetrofitManager.service.getApkTypes()

        doRequest(app, object : Callback<CommonData>(view,screen) {
            override fun failed(tBaseResult: BaseResult<CommonData>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<CommonData>) {

            }

        }, true)
    }


    fun getAppList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getApks(map)

        doRequest(app, object : Callback<AppList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AppList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<AppList>) {
                if (tBaseResult.data!=null)
                    view.onAppList(tBaseResult.data)
            }

        }, true)
    }


    fun buyApk(map: HashMap<String, Any> ) {

        val requestBody= RequestUtils.getBody(map)
        val download = RetrofitManager.service.onBuy(requestBody)

        doRequest(download, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }

        }, true)

    }

}