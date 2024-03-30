package com.bll.lnkteacher.base

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.presenter.CloudUploadPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.common_fragment_title.*


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView {

    var mCloudUploadPresenter= CloudUploadPresenter(this)
    var grade=SPUtil.getInt("grade")
    var popGrades= mutableListOf<PopupBean>()

    override fun onSuccessCloudUpload(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    override fun initView() {
        if (grade>0){
            tv_grade.text= DataBeanManager.getGradeStr(grade)
        }
        popGrades = if (grade==0) DataBeanManager.popupGrades else DataBeanManager.popupGrades(grade)

        tv_grade.setOnClickListener {
            if (popGrades.size==0){
                popGrades =DataBeanManager.popupGrades
            }
            PopupRadioList(requireActivity(), popGrades, tv_grade,tv_grade.width,  5).builder().setOnSelectListener { item ->
                tv_grade?.text=item.name
                grade=item.id
                SPUtil.putInt("grade",grade)
                onGradeEvent()
            }
        }
    }

    open fun onGradeEvent(){

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
