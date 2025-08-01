package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.AccountOrder
import com.bll.lnkteacher.mvp.model.AccountQdBean
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class WalletPresenter(view: IContractView.IWalletView,val screen:Int) : BasePresenter<IContractView.IWalletView>(view) {

    //获取学豆列表
    fun getXdList(boolean: Boolean) {
        val list = RetrofitManager.service.getQdList()
        doRequest(list, object : Callback<MutableList<AccountQdBean>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<AccountQdBean>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<AccountQdBean>>) {
                view.getXdList(tBaseResult.data)
            }

        }, boolean)

    }



    //提交学豆订单
    fun postXdOrder(id:String,payType:Int)
    {
        val post = RetrofitManager.service.postOrder(id,payType)
        doRequest(post, object : Callback<AccountOrder>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.onXdOrder(tBaseResult.data)
            }
        }, true)
    }

    //查看订单状态
    fun checkOrder(id:String)
    {
        val order = RetrofitManager.service.getOrderStatus(id)
        doRequest(order, object : Callback<AccountOrder>(view,screen) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.checkOrder(tBaseResult.data)
            }
        }, false)
    }



}