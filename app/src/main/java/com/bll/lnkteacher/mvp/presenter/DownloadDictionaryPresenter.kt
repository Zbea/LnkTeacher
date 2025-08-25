package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.book.TextbookStore
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager


class DownloadDictionaryPresenter(view: IContractView.IDictionaryResourceView, val screen: Int =0) : BasePresenter<IContractView.IDictionaryResourceView>(view) {

    fun getList(map: HashMap<String,Any>) {
        val books = RetrofitManager.service.getDictionaryList(map)
        doRequest(books, object : Callback<TextbookStore>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TextbookStore>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TextbookStore>) {
                view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun buyDictionary(map: HashMap<String,Any>){
        val body=RequestUtils.getBody(map)
        val buy = RetrofitManager.service.buyBooks(body)
        doRequest(buy, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.buySuccess()
            }
        }, true)
    }

}