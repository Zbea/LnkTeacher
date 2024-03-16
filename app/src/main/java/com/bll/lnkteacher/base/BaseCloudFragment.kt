package com.bll.lnkteacher.base

import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.presenter.CloudPresenter
import com.bll.lnkteacher.mvp.view.IContractView


abstract class BaseCloudFragment : BaseFragment(), IContractView.ICloudView {

    val mCloudPresenter= CloudPresenter(this)
    var types= mutableListOf<String>()

    override fun onList(item: CloudList) {
        onCloudList(item)
    }
    override fun onType(types: MutableList<String>) {
        onCloudType(types)
    }
    override fun onDelete() {
        onCloudDelete()
    }



    /**
     * 获取云数据
     */
    open fun onCloudList(cloudList: CloudList){

    }
    /**
     * 获取云分类
     */
    open fun onCloudType(types: MutableList<String>){

    }
    /**
     * 删除云数据
     */
    open fun onCloudDelete(){

    }

}
