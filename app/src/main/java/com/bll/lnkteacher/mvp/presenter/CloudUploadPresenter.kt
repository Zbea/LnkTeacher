package com.bll.lnkteacher.mvp.presenter

import android.util.Pair
import com.bll.lnkteacher.mvp.model.CloudListBean
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.net.BasePresenter
import com.bll.lnkteacher.net.BaseResult
import com.bll.lnkteacher.net.Callback
import com.bll.lnkteacher.net.RequestUtils
import com.bll.lnkteacher.net.RetrofitManager

class CloudUploadPresenter(view: IContractView.ICloudUploadView):
    BasePresenter<IContractView.ICloudUploadView>(view) {

    fun upload(list:MutableList<CloudListBean>) {
        if (list.isEmpty())
        {
            return
        }
        val body= RequestUtils.getBody(
            Pair.create("listModel",list)
        )
        val type = RetrofitManager.service.cloudUpload(body)
        doRequest(type, object : Callback<MutableList<Int>>(view) {
            override fun failed(tBaseResult: BaseResult<MutableList<Int>>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<MutableList<Int>>) {
                view.onSuccessCloudUpload(tBaseResult.data)
            }
        }, false)
    }

    fun deleteCloud(ids:List<Int>) {
        val body = RequestUtils.getBody(
            Pair.create("ids", ids.toIntArray())
        )
        val delete = RetrofitManager.service.deleteCloudList(body)

        doRequest(delete, object : Callback<Any>(view) {
            override fun failed(tBaseResult: BaseResult<Any>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<Any>) {
            }
        }, false)

    }

}