package com.bll.lnkteacher.mvp.presenter

import com.bll.lnkteacher.mvp.model.HandoutsList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

class TextbookPresenter(view: IContractView.ITextbookView):BasePresenter<IContractView.ITextbookView>(view) {

    //获取讲义列表
    fun getHandoutsList(map: HashMap<String,Any>){
        val list=RetrofitManager.service.getHandouts(map)
        doRequest(list, object : Callback<HandoutsList>(view) {
            override fun failed(tBaseResult: BaseResult<HandoutsList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<HandoutsList>) {
                if (tBaseResult.data!=null)
                    view.onHandoutsList(tBaseResult.data)
            }
        },true)
    }

    /**
     * 添加作业本
     */
    fun addType(map: HashMap<String,Any>) {
        val body= RequestUtils.getBody(map)
        val type = RetrofitManager.service.addPaperType(body)
        doRequest(type, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onAddHomeworkBook()
            }
        }, true)
    }

}