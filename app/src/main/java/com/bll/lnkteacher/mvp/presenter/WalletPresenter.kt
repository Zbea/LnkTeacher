package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.AccountList
import com.bll.lnkteacher.mvp.model.AccountOrder
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RetrofitManager


class WalletPresenter(view: IContractView.IWalletView) : BasePresenter<IContractView.IWalletView>(view) {

    //获取学豆列表
    fun getXdList(boolean: Boolean) {

        var map=HashMap<String,String>()
        map.put("pageIndex", "1")
        map.put("pageSize", "10")

        val list = RetrofitManager.service.getSMoneyList(map)
        doRequest(list, object : Callback<AccountList>(view) {
            override fun failed(tBaseResult: BaseResult<AccountList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountList>) {
                view.getXdList(tBaseResult.data)
            }

        }, boolean)

    }



    //提交学豆订单
    fun postXdOrder(id:String)
    {
        val post = RetrofitManager.service.postOrder(id)
        doRequest(post, object : Callback<AccountOrder>(view) {
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
        doRequest(order, object : Callback<AccountOrder>(view) {
            override fun failed(tBaseResult: BaseResult<AccountOrder>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<AccountOrder>) {
                view.checkOrder(tBaseResult.data)
            }
        }, false)
    }



}