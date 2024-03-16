package com.bll.lnkteacher.base

import com.bll.lnkteacher.mvp.presenter.CloudUploadPresenter
import com.bll.lnkteacher.mvp.view.IContractView


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView {

    var mCloudUploadPresenter= CloudUploadPresenter(this)

    override fun onSuccess(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    /**
     * 上传成功(书籍云id) 上传成功后删掉重复上传的数据
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
        if (!cloudIds.isNullOrEmpty())
        {
            mCloudUploadPresenter.deleteCloud(cloudIds)
        }
    }

}
