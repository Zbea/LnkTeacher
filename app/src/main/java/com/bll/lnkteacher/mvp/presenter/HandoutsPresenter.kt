package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.HandoutsList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager

class HandoutsPresenter(view: IContractView.IHandoutsView):BasePresenter<IContractView.IHandoutsView>(view) {

    fun getList(map: HashMap<String,Any>){
        val list=RetrofitManager.service.getHandouts(map)
        doRequest(list, object : Callback<HandoutsList>(view) {
            override fun failed(tBaseResult: BaseResult<HandoutsList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HandoutsList>) {
                if (tBaseResult.data?.list!=null)
                    view.onList(tBaseResult.data)
            }
        },true)
    }

}