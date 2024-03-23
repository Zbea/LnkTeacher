package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

class TextbookPresenter(view: IContractView.ITextbookView,val screen:Int):BasePresenter<IContractView.ITextbookView>(view) {

    /**
     * 添加作业本
     */
    fun addType(map: HashMap<String,Any>) {
        val body= RequestUtils.getBody(map)
        val type = RetrofitManager.service.addPaperType(body)
        doRequest(type, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAddHomeworkBook()
            }
        }, true)
    }

}