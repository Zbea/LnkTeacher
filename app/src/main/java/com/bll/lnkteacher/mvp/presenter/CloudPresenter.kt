package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.*

class CloudPresenter(view: IContractView.ICloudView,val screen:Int) : BasePresenter<IContractView.ICloudView>(view){

    /**
     * 获取分类
     */
    fun getType(type: Int) {
        val map=HashMap<String,Any>()
        map["type"]=type
        val manager = RetrofitManager.service.getCloudType(map)
        doRequest(manager, object : Callback<MutableList<String>>(view,screen) {
            override fun failed(tBaseResult: BaseResult<MutableList<String>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<String>>) {
                if (tBaseResult.data!=null)
                    view.onType(tBaseResult.data)
            }
        }, true)
    }

    /**
     * 获取列表
     */
    fun getList(map :HashMap<String,Any>) {
        val type = RetrofitManager.service.getCloudList(map)
        doRequest(type, object : Callback<CloudList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<CloudList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<CloudList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        }, true)
    }

    fun deleteCloud(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteCloudList(body)

        doRequest(delete, object : Callback<Any>(view,screen) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
                view.onDelete()
            }
        }, true)

    }

}