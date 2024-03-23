package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.CalenderList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

/**
 * 应用相关
 */
class CalenderPresenter(view: IContractView.ICalenderView) : BasePresenter<IContractView.ICalenderView>(view) {

    fun getList(map: HashMap<String,Any>) {

        val app = RetrofitManager.service.getCalenderList(map)

        doRequest(app, object : Callback<CalenderList>(view) {
            override fun failed(tBaseResult: BaseResult<CalenderList>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<CalenderList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }

        }, true)
    }

    fun buyApk(map: HashMap<String, Any> ) {

        val requestBody= RequestUtils.getBody(map)
        val download = RetrofitManager.service.onBuy(requestBody)

        doRequest(download, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }

            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }

        }, true)

    }

}